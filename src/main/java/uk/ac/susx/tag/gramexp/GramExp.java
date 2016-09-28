package uk.ac.susx.tag.gramexp;

import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.common.ImmutableList;
import org.parboiled.errors.ErrorUtils;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParseTreeUtils;
import org.parboiled.support.ParsingResult;
import org.parboiled.support.ValueStack;
import org.parboiled.transform.AsmUtils;
import uk.ac.susx.tag.gramexp.loading.ClassReloader;

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
public class GramExp implements AutoCloseable {


    private final String grammar;
    private final String className;
    private final String entryPoint;
    private final Path sourcePath;
    private final Path basePath;
    private final Path classPath;
    private final Path classesPath;

    private CapturingParser parser;
    private Method entryPointMethod;

    private Thread task;

    public GramExp(String grammar)  {
        this.grammar = grammar;

        basePath = Util.getUniqueTempDir();
        className = Util.getUniqueClassName();

        Matcher match = Pattern.compile("(?m)(\\w+)\\s*<-.+").matcher(grammar);

        if (!match.find()) {
            throw new GrammarException("can't find entry point $grammar");
        }

        entryPoint = match.group(1);

        sourcePath = basePath.resolve("source");

        classesPath = basePath.resolve("classes");

        classPath = sourcePath.resolve(className + ".java");

        try {

            Files.createDirectories(classesPath);
            Files.createDirectories(sourcePath);
        } catch (IOException e) {
            throw new GrammarException(e);
        }



        Thread task = new Thread(()->{
            generateCode();
            compile();
            load();
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());

        });

        task.run();
        try {
            task.join();
        } catch (InterruptedException e) {
            //pass
        }



    }

    void generateCode() {

        PegParser parser  = Parboiled.createParser(PegParser.class);

        AstToJava astToJava = new AstToJava(className);

        String java = astToJava.toJava(parser.parse(grammar));

//        System.out.println(java);

        try (
            BufferedWriter writer = Files.newBufferedWriter(classPath)
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
                    classPath.toString()
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
                ClassReloader cl1 = new ClassReloader(classesPath, "1");
                ClassReloader cl2 = new ClassReloader(classesPath, "2")
            ) {

                Class<? extends CapturingParser> parserClass = (Class<? extends CapturingParser>) cl1.loadClass(className);

                Thread.currentThread().setContextClassLoader(cl2);

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
        if(parser.getContext()!=null) {
            ValueStack<?> valueStack = parser.getContext().getValueStack();
            while(true) {
                try {
                    captures.add((Capture)valueStack.pop());
                } catch (IllegalArgumentException e) {
                    break;
                }
            }
            Collections.reverse(captures);
        }
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

//        Properties prop = System.getProperties();
//        prop.list(System.out);


//        final String grammar =
//                "/nlp/\n" +
//                        "T <- (Q A)+\n" +
//                        "Q <- <Text<'?'> 'Qu'> S\n" +
//                        "A <- <Text<End> 'An'>\n" +
//                        "End <- '.' / $";
//
//        try (
//                GramExp peg = new GramExp(grammar);
//        ) {
//                for(String input : new String[]{"hello? yes"}) {
//
//                    System.out.println(peg.parse(input));
//                }
//        }

//        final String grammar2 =
//                "/nlp/\n" +
//                "document <- (tag / text)* $\n" +
//                "tag <- open_tag (text / tag)* close_tag / self_close \n" +
//                "open_tag <- '<' tag_type push (S attr)? '>'\n" +
//                "close_tag <- '</' tag_type pop '>'\n" +
//                "self_close <- '<' tag_type '/'? '>'\n" +
//                "attr <- <[0-9a-zA-Z =\"'#\\[-]+ 'attr'>\n" +
//                "tag_type <- <[0-9a-zA-Z]+ 'tag'>\n" +
//                "text <- <(!'<'.)+ 'content'>";
        final String grammar2 =
                "/nlp/\n" +
                "X <- Item+ $" +
                "Item <- Text<Start>? Start Text<Start>" +
                "Start <- !^ [0-9] ";

        try (
                GramExp gramExp = new GramExp(grammar2);
        ) {

                System.out.println(gramExp.groups());
//                for(String input : new String[]{"<html><body>content<br>new line<br/>another line<br>badgers</body></html>"}) {
                for(String input : new String[]{"" +
                        "1. one2two\n" +
                        "3. two"}) {
                    System.out.println(gramExp.parse(input));

                    System.out.println(gramExp.find(input));
                }
        }

    }


    @Override
    public void close() throws Exception {
        Util.deleteFileOrFolder(basePath);
        parser = null;
        entryPointMethod = null;
        AsmUtils.clearClassCache();
//        Thread.currentThread().setContextClassLoader(Class.forName(this.getClass().getName()).getClassLoader());
    }
}
