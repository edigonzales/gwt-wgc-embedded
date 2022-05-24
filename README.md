# gwt-wgc-embedded

## TODO
- **Tests:** Der Client muss Javascript verstehen, sonst sieht man nur den "noscript"-Inhalt. Alle möglichen Webclients durchprobiert. Auch HtmlUnit funktioniert nicht, da fetch() nicht implementiert ist. -> Playwright?
- Externe WMS für Hintergrundkarte
- Fehlermeldung falls keine bigMapLink
- Wie ist das mit GetFeatureInfo bei externen WMS. Ist? Soll?

## Development

First Terminal:
```
./mvnw spring-boot:run -Penv-dev -pl *-server -am 
```

Second Terminal:
```
mvn gwt:codeserver -pl *-client -am
```

## Build
Wenn die Tests (siehe TODO) funktionieren, muss das JS-Zeugs zum Server kopiert werden. Entweder prüft man gleich das Fatjar oder man muss zuerst Paketieren, Testen und nochmals Paketieren (Befehle leicht abhängig, ob für native oder jvm). Noch nicht ganz ausgeklügelt.

### JVM
...

### Native
```
./mvnw clean test
./mvnw -DskipTests -Penv-prod,native package
```


FIXME:
Build fat jar and docker image:
```
BUILD_NUMBER=9999 mvn package
```

## Run
```
docker run -p 8080:8080 sogis/wgc-embedded
```

Die Env-Variable `SPRING_PROFILES_ACTIVE` steuert das Spring-Boot-Profil. Im Profil (application-_profile_name_.yml) sind die Basis-Url der Dienste definiert. Es stehen `dev`, `int`, `test` und `prod` zur Verfügung. Ist die Env-Variable nicht vorhanden, wird `prod` verwendet.

## Anleitung

### Funktionen
Es können die Hintergrundkarten unseres WMTS ausgewählt (`bgLayer`) werden. Es stehen sämtliche Kartenebenen unseres WMS zur Verfügung. Ebenfalls können externe WMS-Layer hinzugeladen werden (siehe Beispiel-Requests). Objektinformationen von externe Kartenebenen werden nicht unterstützt. Ein Link verweist auf den "Original"-Web-GIS-Client. Der Kontext wird mitgeliefert. 

### Parameter

- Die Basis-URL lautet: https://geo.so.ch/api/embed/v1/index.html
- `bgLayer`: Zwingend. Wählt die Hintergrundkarte aus. Es stehen die gleichen Hintergrundkarten wie im Web GIS Client zur Verfügung. Die Namen entsprechen den WMTS-Layernamen.
- `layers`: Optional. Kommaseparierte Liste für die anzuzeigenden Kartenebenen. Die Namen entsprechen den WMS-Layername. Der WMS-Layername entspricht dem Identifier wie er auch in der URL des Web GIS Clients für die jeweilige Kartenebene sichtbar ist.
- `layers_opacity`: Zwingend, falls layers vorhanden. Kommaseparierte Liste mit der Opazität des jeweiligen Layers von layers. Der Wertebereich geht von 0 (komplett durchsichtig resp. unsichtbar) bis 1 (keine Transparenz).
- `E` / `N`: Optional. Koordinatewerte des Kartenmittelpunktes.
- `zoom`: Optional. Zoomstufe der Karte. Entspricht den Zoomstufen des WMTS

### Beispiele

Lokale Kartenebenen:
```
http://localhost:8080/index.html?bgLayer=ch.so.agi.hintergrundkarte_ortho&layers=ch.so.agi.av.grundstuecke,ch.so.agi.av.fixpunkte&layers_opacity=1,1&E=2607457.049140623&N=1228667.6838281231&zoom=14
```

```
<iframe src='http://localhost:8080/index.html?bgLayer=ch.so.agi.hintergrundkarte_ortho&layers=ch.so.agi.av.grundstuecke,ch.so.agi.av.fixpunkte&layers_opacity=1,1&E=2607457.049140623&N=1228667.6838281231&zoom=14 width='600' height='450' style='border:0px solid white;'></iframe>
```

```
http://localhost:8080/index.html?bgLayer=ch.so.agi.hintergrundkarte_sw&layers=ch.so.afu.erdwaermesonden.abfrageperimeter,ch.so.afu.gewaesserschutz.zonen_areale,ch.so.afu.altlasten.standorte&layers_opacity=0.35,0.5,0.6&E=2607577.014463918&N=1227885.4209060299&zoom=11
```

Externe Kartenebene (WMS):
```
http://localhost:8080/index.html?bgLayer=ch.so.agi.hintergrundkarte_ortho&layers=ch.so.agi.av.grundstuecke,WMS%7C%7Chttps:%2F%2Fwfs.geodienste.ch%2Fav_0%2Fdeu%3F%7C%7CHoheitsgrenzen%7C%7C1.3.0&layers_opacity=1,1&E=2607457.049140623&N=1228667.6838281231&zoom=12
```
