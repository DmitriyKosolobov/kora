package ru.tinkoff.kora.kora.app.annotation.processor.extension;

import com.squareup.javapoet.ClassName;
import jakarta.annotation.Nullable;
import ru.tinkoff.kora.annotation.processor.common.CommonUtils;
import ru.tinkoff.kora.annotation.processor.common.NameUtils;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.io.IOException;
import java.util.Set;

public interface KoraExtension {
    @Nullable
    KoraExtensionDependencyGenerator getDependencyGenerator(RoundEnvironment roundEnvironment, TypeMirror typeMirror, Set<String> tags);

    interface KoraExtensionDependencyGenerator {
        ExtensionResult generateDependency() throws IOException;

        static KoraExtensionDependencyGenerator generatedFrom(Elements elements, Element element, ClassName postfix) {
            return generatedFrom(elements, element, postfix.simpleName());
        }

        static KoraExtensionDependencyGenerator generatedFrom(Elements elements, Element element, String postfix) {
            var mapperName = NameUtils.generatedType(element,postfix);
            var packageElement = elements.getPackageOf(element);

            return () -> {
                var maybeGenerated = elements.getTypeElement(packageElement.getQualifiedName() + "." + mapperName);
                if (maybeGenerated != null) {
                    var constructors = CommonUtils.findConstructors(maybeGenerated, m -> m.contains(Modifier.PUBLIC));
                    if (constructors.size() != 1) throw new IllegalStateException();
                    return ExtensionResult.fromExecutable(constructors.get(0));
                }
                return ExtensionResult.nextRound();
            };
        }
    }
}
