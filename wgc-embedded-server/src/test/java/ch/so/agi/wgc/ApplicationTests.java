package ch.so.agi.wgc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ApplicationTests {

    @LocalServerPort
    private int port;
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void contextLoads() {
    }
    
    @Test
    public void index_Ok() throws Exception {
        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/index.html?bgLayer=ch.so.agi.hintergrundkarte_ortho&layers=ch.so.agi.av.grundstuecke,ch.so.agi.av.fixpunkte&layers_opacity=1,1&E=2607457.049140623&N=1228667.6838281231&zoom=14", String.class))
                .contains("Web GIS Client • Kanton Solothurn");
    }
}
