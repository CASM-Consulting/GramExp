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
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
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
    private final Path sourcePath;
    private final Path basePath;
    private final Path grammarPath;
    private final Path classesPath;

    private CapturingParser<?> parser;
    private Method entryPointMethod;

    public Peg(String grammar)  {
        this.grammar = grammar;

        basePath = Util.getTempUniqueDir();

        Matcher match = Pattern.compile("(?m)(\\w+)\\s*<-.+").matcher(grammar);

        if (!match.find()) {
            throw new GrammarException("can't find entry point $grammar");
        }

        entryPoint = grammarName = match.group(1);

        sourcePath = basePath.resolve("source");

        classesPath = basePath.resolve("classes");

        grammarPath = sourcePath.resolve(grammarName + ".java");

        try {

            Files.createDirectories(classesPath);
            Files.createDirectories(sourcePath);
        } catch (IOException e) {
            throw new GrammarException(e);
        }

        generateCode();
        compile();
        load();
    }

    void generateCode() {

        PegParser parser  = Parboiled.createParser(PegParser.class);

        AstToJava astToJava = new AstToJava(grammarName);

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
                grammarPath.toString()
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

                Class<? extends CapturingParser> parserClass = (Class<? extends CapturingParser>) cl2.loadClass(grammarName);
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

    public List<Capture> find(String input) {
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
                "D <- Q A $\n" +
                "Q <- <Text<'?'> 'question'> S?\n" +
                "A <- <(Ic<'yes'> / Ic<'y'> / Ic<'no'> / Ic<'n'> ) 'answer'>";

        try (
                Peg peg = new Peg(grammar2);
        ) {

            System.out.println(peg.groups());
            for(String input : new String[]{"hello? no"}) {
                System.out.println(peg.parse(input));

                System.out.println(peg.find(input));
            }
        }
    }


    @Override
    public void close() throws Exception {
        Util.deleteFileOrFolder(basePath);
    }
}
