package de.torbenphotography.images;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/*
 * The image directory may be created only after the application has started, e.g. on a server
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ImageResourceTest {

    private static final Path IMAGE_DIRECTORY = createMissingDirectory();

    @LocalServerPort
    private int port;

    @Autowired
    private ImageService imageService;

    @DynamicPropertySource
    static void imageDirectory(DynamicPropertyRegistry registry) {
        registry.add("app.images.dir", IMAGE_DIRECTORY::toString);
    }

    @Test
    void servesImagesOfDirectoryCreatedAfterStart() throws Exception {
        Path cover = IMAGE_DIRECTORY.resolve("auto/cover/test.svg");
        Files.createDirectories(cover.getParent());
        Files.writeString(cover, "<svg xmlns=\"http://www.w3.org/2000/svg\"/>");

        assertThat(imageService.findFirst(PhotoCategory.AUTO.cover())).contains("images/auto/cover/test.svg");
        HttpResponse<String> response = HttpClient.newHttpClient().send(
                HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/images/auto/cover/test.svg")).build(),
                HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("<svg");
    }

    private static Path createMissingDirectory() {
        try {
            return Files.createTempDirectory("bilder-test").resolve("bilder");
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
