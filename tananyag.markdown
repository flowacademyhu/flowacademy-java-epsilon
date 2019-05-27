Kollekció (gyűjtemény) osztályok
================================

Javanak sok beépített osztálya van, amelyek objektumai kollekció 
funkcionalitást valósítanak meg, vagyis további objektumokat 
tartalmaznak. Ezek mindegyike megvalósítja a `java.util.List` (lista), 
`java.util.Set` (halmaz), vagy a `java.util.Map` (leképezés) 
interfészeket. A lista sorbarendezett gyűjteményt képvisel, amelyben 
minden elemnek indexe van. Az első elem indexe 0, az utolsó elem indexe 
a lista mérete minusz egy. Azonos elemek előfordulhatnak benne többször.
A halmaz nincs sorbarendezve, és azonos elemek nem fordulhatnak elő 
benne többször. A leképezés kulcs-érték párokat reprezentál. A kulcsok 
egyediek, az értékek nem. A lista és a halmaz bejárhatók 
```
  for(var elem: kollekció) {
     ...
  }
```
típusú for ciklussal, mivel megvalósítják az `Iterable` interfészt. A
leképezés közvetlenül nem járható be így, ellenben biztosít három 
metódust különböző nézetekhez: a `keySet()` a kulcsok halmazát adja meg,
a `values()` az értékek kollekciója, míg az `entrySet()` a kulcs-érték 
párok halmaza, elemei megvalósítják a `Map.Entry` interfészt. Ezek 
mindegyike már bejárható a fenti `for` ciklussal.

Lista megvalósítások
--------------------
Két fő lista megvalósítás a `java.util.ArrayList` és a `java.util.LinkedList`. Az `ArrayList` (tömblista) egy tömbben tárolja az elemeit. Ha a tömb betelik, akkor új, nagyobb tömböt foglal le és abba másolja át az elemeket. A `LinkedList` (láncolt lista) kis láncobjktumokat hoz létre, mindegyik tartalmaz egy elemet, meg két pointert: egyet az előző, egyet meg a következő elemre. A két osztály közötti legfőbb különbségek:
* A tömblistában indexelt olvasás és írás `get(index)` és `set(index, elem)` metódussal konstans időt vesz igénybe. A láncolt listában ugyanez lineáris időt vesz igénybe.
* Láncolt lista elejére és végére való beszúrás konstans időt vesz igénybe. A tömblistá végébe való beszúrás nagyjából konstans (kivéve ha új tömbbe kell átmásolni a régi elemekt). A tömblista elejére való beszúrás viszont lineáris.
* A tömbelemnek jellemzően kevesebb memóriára van szüksége ugyanannyi elem eltárolásához mint a láncolt listának.
* Láncolt lista hatékonyan szerkeszthető, ha kérünk tőle egy `ListIterator` objektumot a `listIterator()` metódussal, és azzal végigjárjuk majd szükség szerint használjuk rajta a `set()`, `remove()` és `add()` metódusokat. A tömblistára továbbra is igaz, hogy minél inkább az eleje felé szúrunk be vagy törlünk elemet, annál lassabb lesz a művelet.

Általánosságban, ha nem világos, hogy mely lista megvalósítás lesz alkalmasabb, érdemes inkább `ArrayList`-et választani.

Leképezés megvalósítások
------------------------
Gyakorlatilag, ha nincs semmi egyéb megkötés, akkor `java.util.HashMap` a legjobb választás. Ez egy hatékony tördelőtábla megvalósítás, amiben ha a benne tárolt kulcsok osztálya jól valósítja meg a `hashCode()` metódust, akkor többnyire konstans idejű minden művelet benne. A  `keySet()`, `values()` és `entrySet()` által visszaadott kollekciókon az elemek bejárásának sorrendje (for ciklussal vagy iterátorral) nem meghatározott.

Ha szükségünk van arra, hogy a beszúrás sorrendjében járjuk be az elemeket, akkor használhatjuk a `java.util.LinkedHashMap` osztályt helyette. Ez arra is használható, hogy un. LRU ("Least Recently Used" - "legrégebben használt") cache-t alakítsunk ki, ha származtatunk belőle osztályt és a `removeEldestEntry` metódusát felüldefiniáljuk arra, hogy régi elemeket automatikusan törölje magából.

Végül, ha a kulcsokon egy meghatározott rendezés szerint akarjuk tárolni az elemeket, akkor használhatjuk a `java.util.TreeMap` osztályt. Ez belülről nem tördelőtáblát használ, hanem egy fa struktúrát, ezáltal a beszúrás és keresés egyaránt logaritmikus időben futnak le, nem pedig konstans időben. Ezen kívül a rendezés miatt szükség van arra, hogy a kulcsként használt osztály megvalósítsa a `java.lang.Comparable` interfészt, vagy alternatívaként a `TreeMap`-nek meg kell adni egy `java.util.Comparator` objektumot, ami a rendezést definiálja.

Halmaz megvalósítások
---------------------
A leképezésekhez hasonlóan létezik `java.util.HashSet` és `java.util.TreeSet`. Minden igaz rájuk amit a leképezéseknél leírtunk, itt csak az a különbség, hogy a halmaz nem rendel értékeket a kulcsokhoz, hanem lényegében olyan, mintha csak a kulcsokat tartalmazná.

További kollekciók
------------------
Java-ban vannak még további kollekciót, például sorok amelyek mindegyike megvalósítja a `java.util.Queue` (egyvégű sor) illetve a `java.util.Deque` (kétvégű sor) interfészt. Ezekkel most nem foglalkozunk. Ezen kívül vannak olyan kollekciók, amelyek biztonságosan használhatók többszálas végrehajtásnál, ezekkel majd foglalkozunk amikor a többszálúságot tárgyaljuk.

Java Serialization
==================
A serialization egy mechanizmus, amivel ez objektumok kiírhatók egy külső bináris reprezentációba, illetve abból beolvashatók. Ez a bináris reprezentáció eltárolható fájlban, átküldhető hálózaton, stb. Ahhoz, hogy egy osztály példányai szerializálhatóak legyenek, az osztálynak meg kell valósítania a `java.io.Serializable` interfészt. Az interfésznek nincsenek metódusai, ő un. _jelölő interfész_ (angolul "marker interface") és azt jelzi, hogy az osztály szerzője engedélyezi a szerializálást. 

A szerializáláshoz létre kell hoznunk egy `java.io.ObjectOutputStream`-et, a szerializált forma visszaolvasásához pedig egy `java.io.ObjectInputStream`-et. Ezek létrehozásához mindig adni kell egy alattuk lévő `OutputStream`-et illetve `InputStream`-et amelybe írják/amelyből olvassák a bináris forma bájtjait. Végül az objektumok a stream-be beleírhatók/visszaolvashatók a `writeObject`/`readObject` metódusokkal.

Nem minden osztálynak kell lennie szerializálhatónak, sőt manapság már sokszor ellenjavallott, mivel sok biztonsági hibával járhat. A szerializált alak módosításával majd abból való beolvasással elkorrumpálható az objektumok belső állapota, illetve létrehozhatók példányok úgy is, ahogy azt az osztály készítője nem tervezte volna. Amikor szerializált formából a rendszer visszaolvassa az objektumot, azt konstruktor hívása nélkül hozza létre. 

A problémák kiküszöbölésére több lehetőség van. Egyrészt a szerializálható osztály deklarálhat egy `readObject` nevű (akár privát) metódust, amiben testreszabhatja a saját mezőinek beolvasását. Ha az írásképét is testre kell szabni, akor egy `writeObject` metódust is deklarálni kell. Ha a beolvasott objektumot le kell cserélni más objektumra (jellemzően singleton-ok beolvasásánál le kell cserélni a deszerializált új objektumot az egyetlen singleton-ra), akkor egy `readResolve` nevű metódust kell biztosítani, ha íráskor egy másik objektumot kell kiírni (pl. serialization proxy-k használatánál), akkor pedig egy `writeReplace` nevű metódus szükséges az osztályon. Ezek további részletei a `java.io.Serializable` interfész dokumentációjában találhatók.

XML Olvasás
===========
Java két API-t is tartalmaz XML olvasásra, mindkettő külső szabványokon alapul és így megtalálhatók más nyelvekben is. Egyrészt van lehetőség arra, hogy egy XML dokumentumot beolvassunk a W3C DOM szabványnak megfelelő fastruktúrába, ami feltehetően HTML/JavaScript világból is ismert lehet. Másrészt lehetőség van arra is, hogy az XML dokumentumot végigolvasva strukturális eseményeket kapjunk egy általunk írt eseménykezelőbe a SAX (Simple API for XML) alkalmazásával. Mindkét módszernek vannak előnyei és hátrányai.

DOM fába olvasás
----------------

DOM fába való olvasáshoz a `javax.xml.parsers.DocumentBuilderFactory` osztályból indulunk ki, abból jellemzően kikérünk egy `DocumentBuilder`-t:
```
DocumentBuilder docBuilder = 
    DocumentBuilderFactory
        .newDefaultInstance()
        .newDocumentBuilder();
```
majd a builder-rel létrehozunk egy dokumentum objektumot:
```
Document doc = docBuilder.parse(in);
```
ahol `in` egy forrás: jellemzően input stream, URL, vagy `java.io.File`.
Ebből utána elkérhető a gyökérelem `getDocumentElement` metódussal, majd
belőle rendelkezésre állnak különböző módszerek a bejárására. A legáltalánosabb a `getChildNodes()` ami az összes benne lévő további csomópontot: beágyazott elemet, szöveget, kommenteket, stb. adja vissza, de lehet célzottan használni a `getElementsByTagName` csak bizonyos nevű
elemek lekérésére. Ami látszik, hogy az API elég régi, pl. a `NodeList` interfész csak `item(index)` meg `getLength()` metódusokkal rendelkezik, nem lehet bejárni for-each ciklussal, stb.

Amit tudni kell még erről az API-ról, hogy az egész dokumentumról épít egy fastruktúrát, így nagy XML dokumentumok beolvasásához sok memória kellhet. Viszont miután be lett olvasva, akárhogyan bejárható, elemezhető, átalakítható.

SAX parszerrel olvasás
----------------------
Itt az előzőhöz hasonlóan egy factory-val kezdünk, csak most egy 
`javax.xml.parsers.SAXParserFactory` a kiindulópont:
```
SAXParser parser = 
    SAXParserFactory
        .newDefaultInstance()
        .newSAXParser();
```
majd ennek is átadunk egy forrást, valamint a DOM-tól eltérően egy eseménykezelőt:
```
parser.parse(in, handler);
```
Az eseménykezelőt nekünk kell megírnunk, jellemzően olyan osztályként ami kiterjeszti az `org.xml.sax.helpers.DefaultHandler` absztrakt osztályt, és meg kell valósítanunk benne olyan metódusokat, amiket a parszer meghív amikor látja egy elemnek a kezdetét vagy a végét, szöveget, stb.

SAX API-val kis, akár fix memóriaigénnyel is olvashatunk végig egy akármekkora méretű XML dokumentumot, ez kétségkívül előnye. Másik előnye, hogy az eseménykezelő metódusaink tetszőleges mélységben megtalálják a keresett elemeket (DOM esetén rekurzív metódusokat kell erre írjunk amik addig mennek lejebb a fastruktúrában, amíg nem találják meg a keresett elemeket.) Hátránya, hogy sok esetben bonyoltultabb megírni egy SAX kezelő kódját, mint azt a kódot, ami végigjár egy felépített DOM fát.


Többszálas végrehajtás
======================

Java természetesen támogatja a többszálas végrehajtást. Alapban a program egy szálon fut: ez az a szál, amin lefut az elindított osztály `main()` metódusa. Azonban Java lehetővé teszi, hogy több szálon egyszerre is futtassunk kódot. Mivel minden modern processzor többmagos, ezzel tudjuk csak igazán jól kihasználni őket, és az összes modern szerverrendszer eleve több szálon teljesíti a bejövő kéréseket. Kliensoldali alkamazásoknál úgyszintén előnyei vannak a többszálas végrehajtásnak, mert lehetőség van arra, hogy hosszabb műveleteket külön szálon, mintegy "háttérben" hajtsuk végre és a programunk UI-ja nem "fagy le" hosszú másodpercekre amíg a művelet az UI eseménykezelő szálán lefut.

A többszálas végrehajtást konkurrensnek is szokás hívni az angol "concurrent" szóbol, aminek magyar jelentése "egy időben történő", vagyis több dolog is egyszerre történhet a programunkban. Ezekkel nem azonos a párhuzamos végrehajtás ("parallel execution") kifejezés, amit különösen csak arra a konkurrens végrehajtásra használunk, amikor ugyanazt a műveletet kell elvégezni több objektumon egyszerre. Vagyis, minden párhuzamos végrehajtás konkurrens, de nem minden konkurrens végrehajtás párhuzamos.

Szálak
------
A többszálas végrehajtás alapvető építőkockája Java-ban a szál, amit a `java.lang.Thread` osztály objektumai képviselnek. Ha egy kódrészletet külön szálon akarunk végrehajtani, létre kell hozni egy új `Thread` objektumot, a végrehajtandó kódot ki kell fejezni egy `java.lang.Runnable` interfész megvalósításával (ez lehet lambda, természetesen), majd elindítani a szál `start()` metódusával. Ezen a ponton a programunk elkezd két szálon futni: a főszál folytatja a start hívása utáni utasítással a saját futását, míg az új szál meghívja a neki átadott `Runnable` `run()` metódusát.

Érdemes tudni, hogy ha elindítottunk egy új szálat is, a Java program nem áll le akkor sem, ha a `main()` metódus véget ér, hanem csak miután az új szál is véget ért a saját futásával. Általában igaz, hogy a Java program egész addig nem lép ki, amíg van benne futó szál.

Tetszőleges mennyiségű szál futhat egyszerre (amíg a gép bírja memóriával).

Ha egy szálban (mondjuk a fő szálban miután elindítottunk továbbiakat) meg akarjuk várni, amíg egy másik szál véget ér, azt megtehetjük a másik szál objektumon meghívott `join()` metódussal. Ez a metódus felfüggeszti a hívó szálat amíg a megcélzott szál véget nem ér.

Végrehajtó szolgáltatások
-------------------------
Modern Java-ban viszonylag ritkán hozunk már létre szálakat. Manapság inkább valahogy szert teszünk egy un. "végrehajtó" objektumra aminek átadunk egy `Runnable`-t lefuttatni. A végrehajtó objektumok megvalósítják a `java.util.concurrent.Executor` interfészt, vagy még gyakrabban az `ExecutorService` interfészt.

Az `java.util.concurrent.Executors` osztály tartalmaz statikus metódusokat, amikkel különböző végrehajtókat lehet gyártani.
* A `newSingleThreadExecutor` olyan végrehajtót hoz létre, ami egyetlen szálon hajt végre (egymás után) minden átadott Runnable-t. 
* A `newCachedThreadPool` olyan végrehajtót hoz létre, ami szükség szerint hoz létre új szálakat, ha több a futtatandó feladat, mint amennyi szabad szála van.
* A `newFixedThreadPool(int size)` metódusnak ezzel szemben át lehet adni egy méretet, és egy olyan végrehajtót hoz létre, ami fix darab szál között osztja szét a futtatandókat. Ez jó választás szerver alkalmazásokra, ahol nem akarjuk, hogy megnövekedett terhelés esetén korlátozás nélkül nőjön a szálak száma.

Közös értékek módosítása
------------------------
Mint azt a `ThreadingExample` nevű kódpéldáink bemutatják, több szálon egyszerre módosítani valamilyen közös értéket rendszerint nem várt eredményeket okoz. Pl. ha 10 szálról egyszerre próbálunk növelni egy `int` értéket ciklusban 3 milliószor, akkor a végső érték nem 30 millió lesz, hanem valamilyen véletlen szám. Ha figyelnénk kívülről a szám változását néha még azt is tapasztalhatnánk, hogy csökken.

A legjobb stratégia az ilyen problémák elkerülésére, ha nem osztunk meg módosítható értékeket szálak között. Ennek a stratégiának a követését sokban megkönnyíti, ha törekszünk nem módosítható objektumokat létrehozni és használni a programunkban - ami nem módosítható, az nyugodtan megosztható szálak között.

Természetesen, nem lehet mindent leredukálni módosíthatatlan objektumokra. Ezekre az esetekre rendelkezésünkre állnak további eszközök, úgy mint atomi értékeket képviselő osztályok, szálbiztos és konkurrens kollekciók, volatilis változók, és a szinkronizálás.

Atomi értékeket képviselő osztályok
-----------------------------------
A `java.util.concurrent.atomic` package-ben találunk különböző `Atomic` kezdetű osztályt, pl. `AtomicLong`. Ennek példányai 64-bites (long) egész számokat tartalmaznak, viszont garantálják, hogy bizonyos műveletek rajtuk mindig atomi, azaz oszthatatlan módon mennek végbe többszálas végrehajtás esetén is. Pl. az `AtomicLong.incrementAndGet` garantáltan eggyel megnöveli a változó értékét. 

Ha kiolvasunk egy értéket, majd azzal számolunk egy új értéket, csak akkor írhatjuk vissza az új értéket a változóba, ha az a kiolvasás óta nem változott. Erre szolgálnak a `compareAndSet` és a `compareAndExchange` metódusok amik garantálják, hogy az összehasonlítás a régi értékkel és az új érték írása egy atomi műveletként zajlnak le. Ha időközben megváltozott az érték, olyankor jellemzően az egész műveletet újracsináljuk az olvasás óta, így a `compareAnd...` metódusok jellemzően `while` ciklusokban fordulnak elő.

Arra az esetre, ha gyakran írunk egy `long` értéket több szálon de ritkábban olvassuk (ilyenek jellemzően mindenféle számlálók, pl. számoljuk, hogy mennyi HTTP kérést teljesítettünk, de azt csak pár másodpercenként olvassuk hogy publikáljuk valami monitorozó alkalmazásba), akkor a `java.util.concurrent.atomic.LongAdder` és a némileg általánosabb `java.util.concurrent.atomic.LongAccumulator` osztályok jó választást jelentenek.

Szálbiztos kollekciók
---------------------
Jellemző használata a különböző `Map` megvalósításoknak, hogy fokozatosan létrehozunk kulcs-érték párokat ha azok még nincsenek benne a leképezésben, vagy valahogy a kulcshoz tartozó értékben gyűjtünk adatokat több menetben. Ilyenkor ahelyett, hogy magunk irnánk olyan kódot, ami kiolvassa az elemet `get()`-tel, majd feltételesen visszaír bele `put()`-tal érdemes inkább használni a `computeIfAbsent`, `compute`, valamint `merge` metódusokat, amik garantálják az ilyen műveletek atomicitását. Az alap `HashMap`, `HashSet`, `ArrayList`, stb. kollekció megvalósítások amik a `java.util` package-ben vannak nem szálbiztosak. Ezeken meghívva a fenti metódusokat ugyan garantálva lesz az atomicitás, de úgy, hogy ha az nem teljesíthető (mert más szál is módosítja az kollekciót menet közben), akkor `ConcurrentModificationException`-t dobnak.

Ha van egy nem szálbiztos kollekciónk, abból tudunk szinkronizáltat faragni belőle a `Collections.synchronized{List|Map|Set}` metódusokkal. (A szinkronizálásról kicsit később.) Ezekre is azonban igaz, hogy nem biztosítják a get-után-put atomicitását, és továbbra is az előbb említett atomi műveletekkel kell rajtuk módosításokat eszközölni. Ezen kívül az ellen az eset ellen sem védenek, amikor az egyik szálban for ciklussal megyünk végig az elemeken, míg egy másikban módosítunk a kollekción, úgyszintén `ConcurrentModificationException`-t fog dobni a for ciklus.

Ha előre tudjuk, hogy egy kollekciót többszálas környezetben fogjuk használni, akkor érdemes olyan osztályt választani, aminek az atomi műveletei eleve biztonságosan használható mindennemű szinkronizálás nélkül. Ilyenek a `java.util.concurrent.ConcurrentHashMap` ami minden körülmény között jól használható, illetve a `java.util.concurrent.CopyOnWriteArrayList` ami egy konkurrens lista, de minden íráskor készít egy másolatot az alatta lévő tömbből, így gyakori módosítás esetén lassú. Érdemes olyan helyzetekben használni (pl. eseménykezelő-listák) ahol ritkán kell módosítani de gyakran kell olvasni.


Szinkronizálás
--------------
Ha egy értéket szeretnénk koordináltan módosítani, akkor arra a fent említett `Atomic…` osztályok valamelyike `compareAndSet` metódussal alkalmas. Ha viszont több mezőnk van, amelyek értékét egyszerre kell módosítani, esetleg több objektumon kell egyszerre módosítást végrehajtani, akkor a kódunkban használni kell a `synchronized` kulcsszót. Java-ban minden objektum tud szolgálni szinkronizációs pontként. Ha egy metódusra tesszük a `synchronized` kulcsszót, az nagyjából azonos azzal, mintha a metódus teste egy `synchronized(this)` blokkban lenne. A szinkronizált blokkba való belépéskor a szál zárolja az objektumot amin szinkronizál, majd kilépéskor feloldja a zárolását. Ha egy másik szál megpróbál belépni szinkronizált blokkba úgy, hogy ugyanarra az objektumra szinkronizál, akkor meg kell várnia, míg a másik szál feloldja a zárolást azzal, hogy kilép az ő aktuális szinkronizált blokkjából. 

Ami fontos, hogy szinkronizálásnál nem a blokk szolgál kizárási egységként, hanem a zárolásra használt objektum. Ha van egy `synchronized(obj)` blokkunk, akkor abban futhat egyszerre két szál, ha különböző objektumokon szinkronizálnak. Fordítva is igaz: ha van két vagy több blokkunk `synchronized(obj)`, az egyik blokkban futó szál kizárhat egy másik blokkba belépni kívánó szálat, ha ugyanarra az objektumra szinkronizálnak.

Szinkronizálásnál fontos szempont, hogy a szinkronizált blokkba belépő szál minden memóriába írás hatását látni fogja, amit az előzőleg ugyanarra az objektumra szinkronizáló szál hajtott végre.

Lock objektumok
---------------
Java-ban a szinkronizálás meglehetősen merev struktúrájú, mindig egy `synchronized` kulcsszóval jelölt kódblokk elején történik a zárolás, majd abból való kilépésnél a feloldás. Ha ennél rugalmasabb megoldásokra van szükség, akkor a `java.util.concurrent.lock` package-ben a `ReentrantLock` nyújt hasonló de mégis rugalmasabb funkcionalitást.

wait/notify
-----------
A szinkronizált blokkban futó szálak a szinkronizálásra használt objektumon tudnak várakozni ha annak mezőinek állapotában való változásra várnak, illetve tudnak jelezni más szálaknak, ha megváltoztatták az állapotot. Erre a minden objektumon meglévő `wait()`, `notify()` és `notifyAll()` metódusok szolgálnak. A jellemző használat, hogy a várakozó szál egy `synchronized` blokkban lévő `while` ciklusban várja amíg elő nem áll a kívánt állapot, míg az állapotot módosító szál szintén `synchronized` blokkban meghívja a `notifyAll` metódust miután módosított olyan mezőt, aminek megváltozott értékére más szál várakozhat.

CountDownLatch és társai
------------------------
A wait/notify mechanizmust ma már ritkán használjuk közvetlenül. Akár csak a szálak kézi létrehozását, amelyek helyett különböző `ExecutorService`-eket használunk, úgy a wait/notify helyett is vannak különböző koordinációs objektumok. Az egyik legegyszerűbb a `java.util.concurrent.CountDownLatch` ami egy egyszeri visszafelé számláló: minden szál ami jelez, hogy készen áll valamilyen részfeladattal (vagy készen áll az indulásra) meghívja rajta a `countDown()` metódust. Egy vagy több szál amik várnak arra, hogy a többi szál fel- vagy elkészüljön közben meghívják az `await()` metódust, ami várni fog, amíg a számláló nem éri el a nullát.

A `CountDownLatch`-en kívül a package-ben található még néhány további szinkronizáló osztály. A `Semaphore` viszonylag gyakran használt, véges számú egyszerre futási engedélyt ad ki. A `CyclicBarrier`-t és a `Phaser`-t csak említjük, ők elég haladó felhasználásúak.

Másik jellemző szálak közötti kommunikáció sorokkal történik: egyes szálak sorokba beletesznek feldolgozandó elemeket, más szálak pedig kiveszik ezeket a sorból és elvégeznek rajtuk valami műveletet. Ezekre érdemes a `java.util.concurrent` package-ben található `BlockingQueue` megvalósítások valamelyikét használni.

Metódustervezés
===============
Amikor metódusokat - ebbe a konstruktorok is beleértendők - tervezünk, a következő szempontokat érdemes figyelembe venni.

Null szűrése
-------------
Ha az objektumunk paraméterként nem fogadhat el null-t (ez például konstruktorokban mező inicializálására különösöen igaz), akkor az `Objects.requireNonNull` metódust érdemes használni ami `NullPointerException`-t dob ha null-t adunk át neki. Esetleg azt a variánsát érdemes használni, aminek üzenetet is lehet átadni, amiben megnevezhető a null paraméter.

Legtöbb esetben nem érdemes elfogadni a null-t, mert később jelentkező hibákhoz vezet. Ezen kívül az osztályaink kódja sokban leegyszerűsíthető, ha kizárhatjuk, hogy bármely mező null.

Egyéb paraméterek szűrése
--------------------------
Ha a metódusunk nem tud elfogadni tetszőleges értékeket valamelyik paramétereiben (a null-on kívül), akkor meg kell vizsgálni azokat, és nem elfogadható érték esetén dobni kell egy `IllegalArgumentException`-t. Null esetén továbbra is `NullPointerException` a mérvadó.

Paraméterek defenzív másolása
-----------------------------
Ha olyan objektumot fogadunk be paraméterként, amit el kell tároljunk későbbre, és az objektum módosítható, akkor másolatot kell róla készíteni (kivéve, ha teljesen privát metódusról van szó, ahol minden hívását mi írjuk meg). Ha tömböket fogadunk, azokat `clone()` metódussal érdemes lemásolni. Ha `List`, `Set`, `Map`-et kapunk, akkor a `List.copyOf`, `Set.copyOf`, és `Map.copyOf` adekvát, csak olvasható másolatokat hoznak nekünk létre. Ha a másolatokat módosítanunk kell, akkor pl. `new ArrayList(paramList)` vagy `new HashMap(paramMap)`-pel hozhatunk létre új, később módosítható kollekciókat.

Ha az így lemásolandó objektumokon további ellenőrzéseket kell lefuttatni (pl. a tömbön belül nem fogadhatunk el null elemeket), az ellenőrzéseket már a másolaton kell elvégezni, mert azt nem tudja senki az ellenőrzés után vagy alatt módosítani.

Visszatérési értékek defenzív másolása
--------------------------------------
Ha az objektumunknak írunk valamely mezőjére getter-t, és az egy olyan objektumot ad vissza, amelynek az állapota módosítható (például tömb, vagy módosítható lista), azzal újra annak a veszélynek tesszük ki az objektumunkat, hogy a belső állapotát kívülről ellenőrizetlenül módosítani tudják. A megoldás ilyenkor is vagy másolat készítése, vagy módosíthatatlan nézet készítése. Ha tömböt adunk vissza, azt a `clone()` metódussal érdemes lemásolni. Ha listát, halmazt, vagy leképezést adunk vissza, _és az módosítható_ (pl. `ArrayList`, `HashMap`, `HashSet`), akkor akár másolatot adhatunk vissza a már ismertetett `{List|Map|Set}.copyOf()` metódusokkal, vagy egy nem módosítható nézetet adhatunk vissza a `Collections.unmodifiable{List|Map|Set}` metódusokkal. Ez utóbbi módszer időben és memóriában is kedvezőbb. Ha a mezőnkben eleve egy nem módosítható kollekciót tárolunk (például mert a konstruktorban kapottat másoltuk le `copyOf()` metódussal), akkor azt nyugodtan kiadhatjuk a getter-ből, mert azt nem tudja módosítani a kód, ami a getterünket meghívta.

Visszatérési értékek hiányzó adatok esetén
------------------------------------------
Ha egy metódus bizonyos körülmények között nem tud értéket visszaadni, azt többféleképpen lehet kifejezni:
* ha a metódus visszatérési értéke kollekció vagy tömb, akkor üres kollekciót vagy üres tömböt illik visszaadni. Üres kollekciók a `{List|Map|Set}.of()` metódussal készíthetők a legegyszerűbben. Tömböt egyszerűen le lehet foglalni 0 mérettel.
* ha a metódus visszatérési értéke valami egyéb (valamilyen `T` típus), akkor `Optional<T>`-t érdemes használni helyette, hogy jelezzük annak a lehetőségét, hogy az érték hiányzik. A kód ami meghívja ilyenkor rá lesz kényszerítve arra, hogy kezelje a hiányzó érték lehetőségét. (Figyelem: ne hívjunk `Optional.get()`-et egy `Optional`-on, csak ha nagyon tudjuk mit csinálunk! Az semmivel sem jobb, mint ha kezeletlenül hagynánk egy nullt .) Ha a visszatérési érték amúgy `int`, `long`, vagy `double` lenne, akkor `OptionalInt`, `OptionalLong`, illetve `OptionalDouble` használhatók.
* ha az `Optional` valamiért nem járható út, a metódus dobhat egy `NoSuchElementException`-t is. Ez ugyan `RuntimeException` így a fordító nem fogja kényszeríteni a hívót, hogy kezelje, de ettől függően a megoldás helyes lehet, főleg ha programozási hiba olykor vagy olyan paraméterekkel meghívni a metódust, amikor/amikkel nem tud visszaadni értéket.
* végső megoldásként visszaadhatunk nullt (ha nem primitív a visszatérési értékünk). Ez a legkevésbé javasolt megoldás, mert ha ez a null érték továbbterjed a programban, és később `NullPointerException`-t okoz, nehéz lehet lekövetni, hogy hol keletkezett. Sajnos sok beépített Java API null-t ad vissza, pl. `Map.get()`, de ezt a gyakorlatot nem kell utánozni.

Enumok
======
Enum (az angol "enumeration", magyarul: felsorolás szóból ered) a Java válasza arra a problémára, hogy definiálnunk kell olyan típust, aminek véges számú, felsorolható értéke van. Például a francia kártya színeinek értékei az
```
enum Suit {
    HEARTS, SPADES, DIAMONDS, CLUBS
}
```
felsorolással fejezhetők ki. Mielőtt a Java-ban lett volna Enum, többnyire integer konstansokkal volt szokás szimulálni a felsorolásokat, de azoknak több problémája is van:
* ha átrendezzük a sorrendet, már meglévő kód helytelenné válik
* egy metódusnak olyan számértéket is átadhatunk, ami nem felel meg egyik felsorolt konstansnak sem
* véletlenül felcserélehetők a különböző felsorolásba tartozó konstansok. Pl. ha a kártyák színét és számát is `int` konstanssal adjuk meg, akkor `Card(int rank, int suit)` esetén könnyű véletlenül fordított sorrendben megadni a paramétereket. Ha mindkettő enum, akkor ezt a hibát lehetetlen elkövetni: `Card(Rank rank, Suit suit)`.

Hány értéknél érdemes enumot használni? Már kettőnél is tökéletesen értelmes főleg ha belegondolunk, hogy a kételemű enumok helyett sokszor boolean értékeket használunk, és nem feltétlenül világos adott helyzetben, hogy milyen jelentése van egy igaz/hamis értéknek paraméterként. Nevesített enum-mal viszont egyértelmű.

Sőt, van értelme az egyelemű enumnak is: az a jelenleg legbiztonságosabb módja singleton osztály létrehozására. 

Az enumok sokszor használhatóak más objektumok jellemzőinek kifejezésére, olykor kombinálhatóak is. Például egy szövegobjektum stílusai kifejezhetőek lennének a
```
enum TextStyle {
  BOLD, ITALIC, UNDERLINE
}
```
Mivel kombinálhatóak, viszonylag természetes dolog kifejezni a jellemzők kombinációit halmazzal, pl. `Set<TextStyle>`. Java-ban van beépített halmazmegvalósítás enumokra, ami nagyon hatékonyan (legtöbbször egyetlen 64-bites long értéken) tárol egy-egy halmazt. A fenti esetben `EnumSet.of(TextStyle.BOLD, TextStyle.ITALIC)` ad egy halmazt, ami a két enumból áll. Hasonlóképpen létezik egy `EnumMap` osztály is, amivel olyan leképezések hatékonyan reprezentálhatók, amiknek enum kulcsai vannak.

Stream-ek
=========
Java-ban a stream (most nem a `java.io` package-ben lévő bináris stream-ekről beszélünk lényegében elemek (objektumok vagy primitív értékek) sorozata, ami egyszer végigjárható. Sokfajta objektum szolgálhat stream forrásaként: a listáknak és halmazoknak (de még az Optional-nak is) van `stream()` metódusa, tömbökből `Array.stream()` metódussal vagy `Stream.of` metódussal képezhetők. Ha egy fájlból az összes sort be akarjuk olvasni stream-ként, azt a `java.nio.file.Files.lines()` metódussal tehetjük meg. 

Minden stream feldolgozás áll egy stream forrásból, valahány köztes átalakító lépésből, majd egy végső összegző lépésből. Az átalakító lépések jellemzően leképezések (`map`) és szűrések (`filter`). Jellemzőjük, hogy minden átalakító lépés maga is stream-et ad vissza, míg a végső lépések valami egyebet.

A köztes lépések lusta kiértékelésűek: amikor egy stream-re alkalmazunk pl. egy `map` hívást, azzal még nem történik semmi. Majd ha a végső lépés (mondjuk egy `forEach`) elkezdi húzni az elemeket a streamből, akkor a köztes stream-ek egyenként fogják alkalmazni a műveleteket az elemeken.

Az összegző lépés pedig tipikusan a végső köztes stream elemeiből képez valamilyen összesített értéket. Ha a stream elemei számok, ez lehet összeg vagy átlag. Ha a stream elemei összehasonlíthatók, akkor lehet minimum vagy maximum. Lehet egyszerűen megszámlálni őket (`Collectors.count`), vagy összegyűjteni őket listába, esetleg csoportosítva összegyűjteni őket egy Map-be. Esetleg használni rajtuk a `findFirst`, `allMatch`, `anyMatch` metódusokat.

A stream lehet végtelen is: `Stream.iterate` és `Stream.generate` lehetővé teszik végtelen stream-ek definiálását, a `Random.ints()` metódus pedig véletlenszerűen generált egész számok stream-jét adja vissza. Végtelen stream-ek feldolgozásánál valahol jellemzően szükség van egy `limit()` köztes lépés beiktatására, ami elvágja a stream-et megadott elemszám után. 

Ha a stream elemei primitív számok, akkor az `IntStream`, `LongStream`, valamint `DoubleStream` stream-eket illik használni, ezek maguktól is előállnak ha pl. egy stream-en a `map` helyett a `mapTo{Int|Long|Double}` stb. metódusokat használjuk.

A streamek alkalmasak arra, ha egy többlépcsős műveletet el kell végezni sok elemen egyszerre, egy ősszegző művelettel a végén.

Funkcionális interfészek
========================
A streamekkel is, de másutt is használhatók a Java funkcionális interfészei. Ezek mindegyik a `java.util.function` csomagban vannak definiálva. Összesen 43 interfész van, de ezeket nem kell mind megjegyezni, mivel csak néhány főbb típusuk van. A sokaságot leginkább az eredményezi, hogy sok interfésznek van olyan variánsa ami primitív értékeken működik, vagy azokat adja vissza.

Ezeket az interfészeket jellemzően ott használjuk paraméterként metódusokban, ahol lambdákat szeretnénk tudni átadni.

A legjellemzőbb interfész a `Function<T, R>` aminek egy metódusa van: `R apply(T t)`. Ez objektumként reprezentál egy olyan műveletet, ami egy T típusú értéket fogad, majd egy R típusú értéket számol belőle. Ha a függvény pl. `int` típusú értéket fogad, akkor helyette lehet használni az `IntFunction<R>` interfészt. Ha `int` értéket ad vissza, akkor pedig `ToIntFunction<T>` a használatos. Ezen kívül van még `Long` és `Double` specializációja is, így máris hét interfésznél tartunk: `Function`, `IntFunction`, `LongFunction`, `DoubleFunction`, `ToIntFunction`, `ToLongFunction`, `ToDoubleFunction`. A többi interfésznek hasonló specializációik vannak.

Arra az esetre, ha a függvény ugyanabból a típusból ugyanabba a típusba alakít át (vagyis, `Function<T, T>`) létezik egy specializáció, az `UnaryOperator<T>`. Ennek is van int, long, double változata.

Ha egy függvény boolean értéket ad vissza, akkor `Predicate<T>`-vel fejezzük ki. Ezek jellemzően feltételeket fejeznek ki, pl. a stream `filter()` metódusában.

Ha egy függvény két paramétert fogad, akkor arra használhatjuk a `BiFunction<T, U, R>` interfészt. Ennek is vanak `{ToInt|ToLong|ToDouble}` változatai valamint `BiPredicate`.

Ha a két paraméter típusa és a visszatérési érték is ugyanaz, akkor `BiFunction<T, T, T>` helyett használható a `BinaryOperator<T>` interfész. Ennek is vannak `{Int|Long|Double}` változatai.

Speciális eset az a interfész, aminek a metódusa zeró paramétere van, de produkál eredményt. Ez a `Supplier<T>`. Jellemzően késleltetett kiértékeléshez szoktuk használni (ha egy metódus egy `T` típusú érték helyett `Supplier<T>` értéket fogad, akkor azon csak szükség esetén később hívja meg a `get()` metódust, így nem kell a hívó félnek előre kiszámolni az értéket, hanem csak egy objektumot ad át, ami szükség esetén kiszámolja azt). A szokásos int, long, double specializációkon kívül ennek van boolean specializációja is.

A Supplier ellentéte az az interfész, ami nem ad vissza értéket, de fogad paramétert, ez a `Consumer<T>`. Jellemzően pl. a `forEach` paramétere ilyen - bármi, aminek átadunk egy értéket, hogy az csináljon vele valamit mellékhatásként: kiírja fájlba, a képernyőre, átküldi hálózaton, stb. (mivel visszaadni nem tud semmit).

Saját interfészek
-----------------
Bármely interfész, aminek egyetlen metódusa van megadható lambdaként vagy metódushivatkozásként. Felmerül azonban a kérdés, hogy miért definiálnánk egyáltalán saját hasonló interfészeket ha a beépített funkcionális interéfszeket is használhatjuk.

Több oka is lehet a saját interfész definiálásának. Az egyik a dokumentáció. Saját interfésznek lehet dokumentációja, ami főleg akkor lehet fontos, ha megkötések vannak az interfész elvárható működésére nézve. A Java beépített API-jai közül erre nagyon jó példa a `Comparator<T>` interfész. Ha belegondolunk, akárhol előfordul a `Comparator<T>`, ott lehetne használni egy `ToIntBiFunction<T, T>`-t is, mégsem ezt tesszük. Viszont ha ránézünk a Comparator dokumentációjára látjuk, hogy annak van elég sok megkötése, amelyeket valahol érdemes lefektetni. Ezen kívül, ha egy metódusra ránézünk és látjuk, hogy az Comparator-t vár, tudjuk, hogy azt a függvényt összehasonlításra fogja használni.

Van ezen kívül olyan is, hogy az általános interfész típusai túl bonyolultak lesznek, és körülményes minden helyen kiírni őket ahol kell. Ilyen esetben egy saját interfész leegyszerűsíti a programot.

