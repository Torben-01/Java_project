package de.torbenphotography;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.theme.aura.Aura;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.ColorScheme;

@SpringBootApplication
@StyleSheet(Aura.STYLESHEET)
@StyleSheet("styles.css")
@ColorScheme(ColorScheme.Value.LIGHT_DARK)
public class PhotographyApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(PhotographyApplication.class, args);
    }
}
