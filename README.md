# gwt-wgc-embedded

## Info
Zum jetzigen Zeitpunkt bräuchte es domino-ui nicht. Wenn ich aber bloss hal/elemento als Abhängigkeit verwende, fliegt mir die Anwendung um die Ohren. Diese Elemento verwendet eine andere elemental2-Version, die nicht mit GWT 2.8.2 kompatibel ist.

## Development

First Terminal:
```
mvn clean spring-boot:run
```

Second Terminal:
```
mvn gwt:generate-module gwt:codeserver
```

Or simple devmode (which worked better for java.xml.bind on client side):
```
mvn gwt:generate-module gwt:devmode 
``` 

Build fat jar and docker image:
```
BUILD_NUMBER=9999 mvn package
```

## Run
```
docker run -p 8080:8080 sogis/wgc-embedded
```


## Beispiele

```
http://localhost:8080/embed.html?bgLayer=ch.so.agi.hintergrundkarte_ortho&layers=ch.so.agi.av.grundstuecke,ch.so.agi.av.fixpunkte&layers_opacity=1,1&E=2607457.049140623&N=1228667.6838281231&zoom=14
```

```
http://localhost:8080/embed.html?bgLayer=ch.so.agi.hintergrundkarte_sw&layers=ch.so.afu.erdwaermesonden.abfrageperimeter,ch.so.afu.gewaesserschutz.zonen_areale,ch.so.afu.altlasten.standorte&layers_opacity=0.35,0.5,0.6&E=2607577.014463918&N=1227885.4209060299&zoom=11
```
