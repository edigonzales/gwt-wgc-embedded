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


## Varia

- https://geo.so.ch/api/dataproduct/v1/weblayers?filter=ch.so.afu.bodeninformationen.lw.verdichtungsempfindlichkeit,ch.so.afu.bodeninformationen.lw.charakter_wasserhaushalt,ch.so.afu.bodeninformationen.lw.durchwurzelungstiefe,ch.so.afu.bodeninformationen.lw.pflanzennutzbarer_wasservorrat,ch.so.afu.bodeninformationen.lw.bodenart_und_bearbeitbarkeit,ch.so.afu.bodeninformationen.lw.humusgehalt,ch.so.afu.bodeninformationen.lw.ph_oberboden,ch.so.afu.bodeninformationen.lw.steingehalt_oberboden,ch.so.afu.bodeninformationen.lw.nutzungsmoeglichkeiten,ch.so.afu.bodeninformationen.lw.hangneigung,ch.so.afu.bodeninformationen.lw.erosionsgefahr

- http://localhost:8080/?bgLayer=ch.so.agi.hintergrundkarte_sw&layers=ch.so.agi.av.fixpunkte,ch.so.agi.hoheitsgrenzen
- http://localhost:8080/?bgLayer=ch.so.agi.hintergrundkarte_sw&layers=ch.so.afu.bodeninformationen.lw.verdichtungsempfindlichkeit,ch.so.agi.av.fixpunkte&layers_opacity=0.75,1.0
- http://localhost:8080/?bgLayer=ch.so.agi.hintergrundkarte_sw&layers=ch.so.afu.bodeninformationen.lw.verdichtungsempfindlichkeit,ch.so.agi.av.fixpunkte&layers_opacity=0.75,1.0

- https://geo.so.ch/api/v1/featureinfo/somap?service=WMS&version=1.3.0&request=GetFeatureInfo&id=default1587909976334&layers=ch.so.agi.av.fixpunkte&query_layers=ch.so.agi.av.fixpunkte&x=51&y=51&i=51&j=51&height=101&width=101&srs=EPSG:2056&crs=EPSG:2056&bbox=2602209.3625,1229732.5333333334,2602316.2541666664,1229839.425&info_format=text%2Fxml&with_geometry=true&with_maptip=false&feature_count=40&FI_POINT_TOLERANCE=16&FI_LINE_TOLERANCE=8&FI_POLYGON_TOLERANCE=4

- https://geo.so.ch/map/?bl=hintergrundkarte_sw&l=ch.so.afu.bodeninformationen.lw.erosionsgefahr%5B30%5D!%2Cch.so.afu.bodeninformationen.lw.hangneigung%5B30%5D!%2Cch.so.afu.bodeninformationen.lw.nutzungsmoeglichkeiten%5B30%5D!%2Cch.so.afu.bodeninformationen.lw.steingehalt_oberboden%5B30%5D!%2Cch.so.afu.bodeninformationen.lw.ph_oberboden%5B30%5D!%2Cch.so.afu.bodeninformationen.lw.humusgehalt%5B30%5D!%2Cch.so.afu.bodeninformationen.lw.bodenart_und_bearbeitbarkeit%5B30%5D!%2Cch.so.afu.bodeninformationen.lw.pflanzennutzbarer_wasservorrat%5B30%5D!%2Cch.so.afu.bodeninformationen.lw.durchwurzelungstiefe%5B30%5D!%2Cch.so.afu.bodeninformationen.lw.charakter_wasserhaushalt%5B30%5D!%2Cch.so.afu.bodeninformationen.lw.verdichtungsempfindlichkeit%5B30%5D&t=default&c=2618500%2C1238000&s=200000

- https://geo.so.ch/map/?bl=hintergrundkarte_sw&l=ch.so.afu.bodeninformationen.lw.verdichtungsempfindlichkeit%5B30%5D&t=default&c=2625057%2C1240574&s=80000

