package kale.net.json.processor;

import com.jsonformat.JsonParserHelper;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import kale.net.json.annotation.Json2Model;

/**
 * @author Jack Tony
 * @date 2015/8/13
 */
@SupportedAnnotationTypes({"kale.net.json.annotation.Json2Model"})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class Json2ModelProcessor extends AbstractProcessor {

    private static final String TAG = "[ " + Json2Model.class.getSimpleName() + " ]:";

    private Elements elementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
    }

    private String packageName;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement te : annotations) {
            for (Element e : roundEnv.getElementsAnnotatedWith(te)) {
                log("Working on: " + e.toString());
                VariableElement varE = (VariableElement) e;

                Json2Model json2Model = e.getAnnotation(Json2Model.class);
                if (json2Model.packageName().equals("")) {
                    // no custom package name
                    /**
                     *  example:
                     *  String GET_USER_INFO = "create/info/user/info"; 
                     *  result:create/info/user/info
                     */
                    if (varE.getConstantValue() == null) {
                        fatalError("jsonStr couldn't be final");
                    }
                    String url = varE.getConstantValue().toString();
                    packageName = url2packageName(url);
                } else {
                    // has custom package name
                    packageName = json2Model.packageName();
                }
                if (json2Model.jsonStr() == null || json2Model.jsonStr().equals("")) {
                    fatalError("json string is null");
                }

                final String clsName = json2Model.modelName();

                JsonParserHelper helper = new JsonParserHelper();
                helper.parse(json2Model.jsonStr(), clsName, new JsonParserHelper.ParseListener() {
                    public void onParseComplete(String str) {
                        createModelClass(packageName, clsName, "package " + packageName + ";\n" + str);
                    }

                    public void onParseError(Exception e) {
                        e.printStackTrace();
                        fatalError(e.getMessage());
                    }
                });
                log("Complete on: " + e.toString());
            }
        }
        return true;
    }

    private void createModelClass(String packageName, String clsName, String content) {
        //PackageElement pkgElement = elementUtils.getPackageElement("");
        TypeElement pkgElement = elementUtils.getTypeElement(packageName);

        OutputStreamWriter osw = null;
        try {
            // create a model file
            JavaFileObject fileObject = processingEnv.getFiler().createSourceFile(packageName + "." + clsName, pkgElement);
            OutputStream os = fileObject.openOutputStream();
            osw = new OutputStreamWriter(os, Charset.forName("UTF-8"));
            osw.write(content, 0, content.length());

        } catch (IOException e) {
            e.printStackTrace();
            fatalError(e.getMessage());
        } finally {
            try {
                if (osw != null) {
                    osw.flush();
                    osw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                fatalError(e.getMessage());
            }
        }
    }

    private void log(String msg) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, TAG + msg);
    }

    private void fatalError(String msg) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, TAG + " FATAL ERROR: " + msg);
    }

    /**
     * /user/test/ - > user.test
     */
    public static String url2packageName(String url) {
        String packageName = url.replaceAll("/", ".");
        if (packageName.startsWith(".")) {
            packageName = packageName.substring(1);
        }
        if (packageName.substring(packageName.length() - 1).equals(".")) {
            packageName = packageName.substring(0, packageName.length() - 1);
        }
        return packageName;
    }

}
