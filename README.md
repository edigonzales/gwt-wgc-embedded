# gwt-wgc

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
TRAVIS_BUILD_NUMBER=9999 mvn package
```


## Varia

https://geo.so.ch/api/dataproduct/v1/weblayers?filter=ch.so.afu.bodeninformationen.lw.verdichtungsempfindlichkeit,ch.so.afu.bodeninformationen.lw.charakter_wasserhaushalt,ch.so.afu.bodeninformationen.lw.durchwurzelungstiefe,ch.so.afu.bodeninformationen.lw.pflanzennutzbarer_wasservorrat,ch.so.afu.bodeninformationen.lw.bodenart_und_bearbeitbarkeit,ch.so.afu.bodeninformationen.lw.humusgehalt,ch.so.afu.bodeninformationen.lw.ph_oberboden,ch.so.afu.bodeninformationen.lw.steingehalt_oberboden,ch.so.afu.bodeninformationen.lw.nutzungsmoeglichkeiten,ch.so.afu.bodeninformationen.lw.hangneigung,ch.so.afu.bodeninformationen.lw.erosionsgefahr

http://localhost:8080/?bgLayer=ch.so.agi.hintergrundkarte_sw&layers=ch.so.agi.av.fixpunkte