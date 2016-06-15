package uk.ac.susx.tag.peg.parboiled;

import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.common.ImmutableList;
import org.parboiled.errors.ErrorUtils;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParseTreeUtils;
import org.parboiled.support.ParsingResult;
import org.parboiled.support.ValueStack;
import uk.ac.susx.tag.peg.parboiled.loading.ClassReloader;

import javax.tools.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by simon on 09/06/16.
 */
public class Peg implements AutoCloseable {


    private final String grammar;
    private String grammarName;
    private String entryPoint;
    private final Path genSource = Paths.get("target/generated-sources/parboiled");
    private final Path outPath;
    private final Path grammarPath;
    private final Path classesPath;
    private final String pkg;

    private CapturingParser<?> parser;
    private Method entryPointMethod;

    public Peg(String grammar) {
        this("parboiledpeg", grammar);
    }

    public Peg(String pkg, String grammar)  {
        this.grammar = grammar;

        this.pkg = pkg;

        Matcher match = Pattern.compile("(?m)(\\w+)\\s*<-.+").matcher(grammar);

        if (!match.find()) {
            throw new GrammarException("can't find entry point $grammar");
        }

        entryPoint = grammarName = match.group(1);

        outPath = genSource.resolve(pkg.replaceAll("\\.", "/"));
        classesPath = Paths.get("custom/classes");

        grammarPath = outPath.resolve(grammarName + ".java");

        try {

            Files.createDirectories(classesPath);
            Files.createDirectories(outPath);
        } catch (IOException e) {
            throw new GrammarException(e);
        }

        generateCode();
        compile();
        load();
    }

    void generateCode() {

        PegParser parser  = Parboiled.createParser(PegParser.class);

        AstToJava astToJava = new AstToJava(pkg, grammarName);

        String java = astToJava.toJava(parser.parse(grammar));

        try (
            BufferedWriter writer = Files.newBufferedWriter(grammarPath)
            ) {
            writer.write(java);
        } catch (IOException e) {
            throw new GrammarException(e);
        }

    }

    void compile() {

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, Locale.getDefault(), null);

        Iterable<? extends JavaFileObject> javaObjects = fileManager.getJavaFileObjectsFromStrings(
            ImmutableList.of(
                outPath.resolve(grammarName).toString() + ".java"
            )
        );

        String[] compileOptions = new String[]{"-d", classesPath.toFile().getAbsolutePath()};

        Iterable<String> compilationOptions = Arrays.asList(compileOptions);

        JavaCompiler.CompilationTask compilerTask = compiler.getTask(null, fileManager, diagnostics, compilationOptions, null, javaObjects);

        if (!compilerTask.call()) {
            String msg = "";
            for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
                msg += String.format("Error on line %d in %s\n", diagnostic.getLineNumber(), diagnostic);
            }
            throw new GrammarException("Could not compile project \n" + msg);
        }
    }

    void load() {
        try {

//            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{ classesPath.toFile().toURI().toURL() });

            try (
                ClassReloader cl1 = new ClassReloader(classesPath);
                ClassReloader cl2 = new ClassReloader(classesPath)
            ) {

                Class<? extends CapturingParser> parserClass = (Class<? extends CapturingParser>) cl2.loadClass(pkg + "." + grammarName);
                Thread.currentThread().setContextClassLoader(cl1);

                //            parserClass = (Class<? extends CapturingParser> )new ClassReloader(classesPath).loadClass(parserClass.getName());

                parser = Parboiled.createParser(parserClass);

                entryPointMethod = parserClass.getMethod(entryPoint);

            }
        } catch(IOException | NoSuchMethodException e) {
            try {
                close();
            } catch (Exception ee) {

            }
            throw new GrammarException(e);
        }
    }



    public String parse(String input) {
        try {

            ReportingParseRunner runner = new ReportingParseRunner <>((Rule)entryPointMethod.invoke(parser));
            ParsingResult<?> result = runner.run(input);

            if (!result.parseErrors.isEmpty()) {
                throw new GrammarException(ErrorUtils.printParseError(result.parseErrors.get(0)));
            } else {
                return ParseTreeUtils.printNodeTree(result) + '\n';
            }

        } catch (IllegalAccessException | InvocationTargetException e) {

            throw new GrammarException(e);
        }

    }

    public Set<String> groups() {
        return parser.getGroups();
    }


    private List<Capture> captures() {
        List<Capture> captures = new ArrayList<>();
        ValueStack<?> valueStack = parser.getContext().getValueStack();
        while(true) {
            try {
                captures.add(Capture.of((String)valueStack.pop(), (String)valueStack.pop()));
            } catch (IllegalArgumentException e) {
                break;
            }
        }
        Collections.reverse(captures);
        return captures;
    }

    public boolean match(String input) {
        try {
            parse(input);
            return true;
        } catch (GrammarException e) {
            return false;
        }
    }

    public List<?> find(String input) {
        try {
            parse(input);
            return captures();
        } catch (GrammarException e) {
            return null;
        }
    }

    public static void main(String[] args) throws Exception {

        final String grammar =
                "D <- &(A !'b') 'a'* B !." +
                "A <- 'a' A 'b' / :\n" +
                "B <- 'b' B 'c' / :\n";

        try (
            Peg peg = new Peg(grammar);
        ) {
            for(String input : new String[]{"abc", "aabbcc"}) {

                System.out.println(peg.parse(input));
            }
        }

        final String grammar2 =
                "/nlp/\n" +
                "D <- <(Text<'?'> '?') 'question'>";

        try (
                Peg peg = new Peg(grammar2);
        ) {

            System.out.println(peg.groups());
            for(String input : new String[]{"hello?"}) {

                System.out.println(peg.find(input));
            }
        }
    }


    @Override
    public void close() throws Exception {
        Files.delete(grammarPath);
        Files.delete(classesPath.resolve(pkg.replaceAll("\\.", File.separator)).resolve(grammarName+".class"));
    }
}
