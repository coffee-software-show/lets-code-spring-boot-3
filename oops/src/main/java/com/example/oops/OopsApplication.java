package com.example.oops;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.util.SystemPropertyUtils;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Set;

@SpringBootApplication
@ImportRuntimeHints(Hints.class)
@RegisterReflectionForBinding(Customer.class)
public class OopsApplication {

    public static void main(String[] args) {
        SpringApplication.run(OopsApplication.class, args);
    }

    @Bean
    ApplicationRunner myRunner(Greeter greeter, ObjectMapper objectMapper) {
        return args -> {
            var customers = Set.of(new Customer(1, "A"), new Customer(2, "B"));
            System.out.println(objectMapper.writeValueAsString(customers));
            try (var oos = new ObjectOutputStream(new FileOutputStream(SystemPropertyUtils.resolvePlaceholders("${HOME}/Desktop/out")))) {
                oos.writeObject(customers);
            }
            Set.of(greeter).forEach(g -> g.greet("world"));
        };
    }

    @Bean
    Greeter greeter() {
        var pfb = new ProxyFactoryBean();
        pfb.addInterface(Greeter.class);
        pfb.addAdvice((MethodInterceptor) invocation -> {
            if (invocation.getMethod().getName().equals("greet"))
                System.out.println((Math.random() > .5 ? "ni hao" : "bonjour") + " "
                                   + invocation.getArguments()[0]);
            return null;
        });
        return (Greeter) pfb.getObject();
    }

    public static interface Greeter {
        void greet(String name);
    }

    @Bean
    static MyBeanFactoryInitializationAotProcessor myBeanFactoryInitializationAotProcessor() {
        return new MyBeanFactoryInitializationAotProcessor();
    }
}


class MyBeanFactoryInitializationAotProcessor implements BeanFactoryInitializationAotProcessor {

    @Override
    public BeanFactoryInitializationAotContribution processAheadOfTime(ConfigurableListableBeanFactory beanFactory) {
        return (generationContext, code) -> {
            var beanNames = beanFactory.getBeanNamesForType(OopsApplication.Greeter.class);
            for (var bn : beanNames)
                System.out.println("bn: " + bn);
            if (beanNames.length > 0) {
                var hints = generationContext.getRuntimeHints();
                hints.proxies().registerJdkProxy(
                        OopsApplication.Greeter.class,
                        org.springframework.aop.SpringProxy.class,
                        org.springframework.aop.framework.Advised.class,
                        org.springframework.core.DecoratingProxy.class
                );
            }

            var methodReference = code.getMethods().add("myMethod", builder -> builder
                    //.addParameter(String.class, "myString")
                    .addCode("System.out.println(\"hello world\");"))
                    .toMethodReference();
            code.addInitializer( methodReference);

        };
    }
}


class Hints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {

        hints.serialization().registerType(Customer.class);
    }
}

record Customer(int id, String name) implements Serializable {
}