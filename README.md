
### Escuela Colombiana de Ingeniería
### Arquitecturas de Software - ARSW
## Ejercicio Introducción al paralelismo - Hilos - Caso BlackListSearch


### Dependencias:
####   Lecturas:
*  [Threads in Java](http://beginnersbook.com/2013/03/java-threads/)  (Hasta 'Ending Threads')
*  [Threads vs Processes]( http://cs-fundamentals.com/tech-interview/java/differences-between-thread-and-process-in-java.php)

### Descripción
  Este ejercicio contiene una introducción a la programación con hilos en Java, además de la aplicación a un caso concreto.
  

**Parte I - Introducción a Hilos en Java**

1. De acuerdo con lo revisado en las lecturas, complete las clases CountThread, para que las mismas definan el ciclo de vida de un hilo que imprima por pantalla los números entre A y B.

	El funcionamiento se puede ver a partir de ejecutar el comando
	```mvn package```
	Segido de la siguiente linea para correr la ejecución:
	``` java -cp target\PiDigits-1.0-SNAPSHOT.jar edu.eci.arsw.threads.CountThreadsMain ```
	
	**Run execution:**
	
	  <img width="232" alt="run_execution" src="https://user-images.githubusercontent.com/49318314/90572880-b3fa3380-e17a-11ea-8f74-e0f87cf12a12.png">
	
	**start execution:**
	
	  <img width="172" alt="start_execution" src="https://user-images.githubusercontent.com/49318314/90572882-b492ca00-e17a-11ea-8442-3829aa672722.png">

2. Complete el método __main__ de la clase CountMainThreads para que:
	1. Cree 3 hilos de tipo CountThread, asignándole al primero el intervalo [0..99], al segundo [99..199], y al tercero [200..299].
	2. Inicie los tres hilos con 'start()'.
	3. Ejecute y revise la salida por pantalla. 
	4. Cambie el incio con 'start()' por 'run()'. Cómo cambia la salida?, por qué?.
	
	La salida se ve alterada en su orden de impresión, esto se debe a que al hacer un 'start()' se ejecuta el hilo, dando posibilidad a la concurrencia, mientras qué, al ejecutar 'run()', se hace llamado diréctamente al método de run(), haciendo un llamado como se haría con cualquier método y esperando a que este termine para continuar con la siguiente instrucción.

**Parte II - Ejercicio Black List Search**


Para un software de vigilancia automática de seguridad informática se está desarrollando un componente encargado de validar las direcciones IP en varios miles de listas negras (de host maliciosos) conocidas, y reportar aquellas que existan en al menos cinco de dichas listas. 

Dicho componente está diseñado de acuerdo con el siguiente diagrama, donde:

- HostBlackListsDataSourceFacade es una clase que ofrece una 'fachada' para realizar consultas en cualquiera de las N listas negras registradas (método 'isInBlacklistServer'), y que permite también hacer un reporte a una base de datos local de cuando una dirección IP se considera peligrosa. Esta clase NO ES MODIFICABLE, pero se sabe que es 'Thread-Safe'.

- HostBlackListsValidator es una clase que ofrece el método 'checkHost', el cual, a través de la clase 'HostBlackListDataSourceFacade', valida en cada una de las listas negras un host determinado. En dicho método está considerada la política de que al encontrarse un HOST en al menos cinco listas negras, el mismo será registrado como 'no confiable', o como 'confiable' en caso contrario. Adicionalmente, retornará la lista de los números de las 'listas negras' en donde se encontró registrado el HOST.

![](img/Model.png)

Al usarse el módulo, la evidencia de que se hizo el registro como 'confiable' o 'no confiable' se dá por lo mensajes de LOGs:

INFO: HOST 205.24.34.55 Reported as trustworthy

INFO: HOST 205.24.34.55 Reported as NOT trustworthy


Al programa de prueba provisto (Main), le toma sólo algunos segundos análizar y reportar la dirección provista (200.24.34.55), ya que la misma está registrada más de cinco veces en los primeros servidores, por lo que no requiere recorrerlos todos. Sin embargo, hacer la búsqueda en casos donde NO hay reportes, o donde los mismos están dispersos en las miles de listas negras, toma bastante tiempo.

Éste, como cualquier método de búsqueda, puede verse como un problema [vergonzosamente paralelo](https://en.wikipedia.org/wiki/Embarrassingly_parallel), ya que no existen dependencias entre una partición del problema y otra.

Para 'refactorizar' este código, y hacer que explote la capacidad multi-núcleo de la CPU del equipo, realice lo siguiente:

1. Cree una clase de tipo Thread que represente el ciclo de vida de un hilo que haga la búsqueda de un segmento del conjunto de servidores disponibles. Agregue a dicha clase un método que permita 'preguntarle' a las instancias del mismo (los hilos) cuantas ocurrencias de servidores maliciosos ha encontrado o encontró.

2. Agregue al método 'checkHost' un parámetro entero N, correspondiente al número de hilos entre los que se va a realizar la búsqueda (recuerde tener en cuenta si N es par o impar!). Modifique el código de este método para que divida el espacio de búsqueda entre las N partes indicadas, y paralelice la búsqueda a través de N hilos. Haga que dicha función espere hasta que los N hilos terminen de resolver su respectivo sub-problema, agregue las ocurrencias encontradas por cada hilo a la lista que retorna el método, y entonces calcule (sumando el total de ocurrencuas encontradas por cada hilo) si el número de ocurrencias es mayor o igual a _BLACK_LIST_ALARM_COUNT_. Si se da este caso, al final se DEBE reportar el host como confiable o no confiable, y mostrar el listado con los números de las listas negras respectivas. Para lograr este comportamiento de 'espera' revise el método [join](https://docs.oracle.com/javase/tutorial/essential/concurrency/join.html) del API de concurrencia de Java. Tenga también en cuenta:

	* Dentro del método checkHost Se debe mantener el LOG que informa, antes de retornar el resultado, el número de listas negras revisadas VS. el número de listas negras total (línea 60). Se debe garantizar que dicha información sea verídica bajo el nuevo esquema de procesamiento en paralelo planteado.

	* Se sabe que el HOST 202.24.34.55 está reportado en listas negras de una forma más dispersa, y que el host 212.24.24.55 NO está en ninguna lista negra.


**Parte II.I Para discutir la próxima clase (NO para implementar aún)**

La estrategia de paralelismo antes implementada es ineficiente en ciertos casos, pues la búsqueda se sigue realizando aún cuando los N hilos (en su conjunto) ya hayan encontrado el número mínimo de ocurrencias requeridas para reportar al servidor como malicioso. Cómo se podría modificar la implementación para minimizar el número de consultas en estos casos?, qué elemento nuevo traería esto al problema?

- Anteriormente se hizo una implementación que ignoraba un mínimo de casos por los cuales el *servidor* podría considerarse como malicioso, por este motivo. a pesar de haber encontrado el servidor en más de 20 casos, este seguía buscando en todos lo servidores, haciéndolo totalmente ineficiente.


- Como propuesta a solucionar este problema, se propone el uso de un ```Atomic Integer``` que permite asegurar que para todos los ```N``` *Threads* se evite una búsqueda innecesariamente larga, de este modo se permite que todos los *Threads* sean consientes de un máximo de casos a evaluar para considear malicioso el servidor.

**Parte III - Evaluación de Desempeño**

A partir de lo anterior, implemente la siguiente secuencia de experimentos para realizar las validación de direcciones IP dispersas (por ejemplo 202.24.34.55), tomando los tiempos de ejecución de los mismos (asegúrese de hacerlos en la misma máquina):

1. Un solo hilo.
2. Tantos hilos como núcleos de procesamiento (haga que el programa determine esto haciendo uso del [API Runtime](https://docs.oracle.com/javase/7/docs/api/java/lang/Runtime.html)).
3. Tantos hilos como el doble de núcleos de procesamiento.
4. 50 hilos.
5. 100 hilos.

Al iniciar el programa ejecute el monitor jVisualVM, y a medida que corran las pruebas, revise y anote el consumo de CPU y de memoria en cada caso. ![](img/jvisualvm.png)

Con lo anterior, y con los tiempos de ejecución dados, haga una gráfica de tiempo de solución vs. número de hilos. Analice y plantee hipótesis con su compañero para las siguientes preguntas (puede tener en cuenta lo reportado por jVisualVM):



1. Según la [ley de Amdahls](https://www.pugetsystems.com/labs/articles/Estimating-CPU-Performance-using-Amdahls-Law-619/#WhatisAmdahlsLaw?):

	![](img/ahmdahls.png), donde _S(n)_ es el mejoramiento teórico del desempeño, _P_ la fracción paralelizable del algoritmo, y _n_ el número de hilos, a mayor _n_, mayor debería ser dicha mejora. Por qué el mejor desempeño no se logra con los 500 hilos?, cómo se compara este desempeño cuando se usan 200?. 

2. Cómo se comporta la solución usando tantos hilos de procesamiento como núcleos comparado con el resultado de usar el doble de éste?.

3. De acuerdo con lo anterior, si para este problema en lugar de 100 hilos en una sola CPU se pudiera usar 1 hilo en cada una de 100 máquinas hipotéticas, la ley de Amdahls se aplicaría mejor?. Si en lugar de esto se usaran c hilos en 100/c máquinas distribuidas (siendo c es el número de núcleos de dichas máquinas), se mejoraría?. Explique su respuesta.

### A Single Thread
Con un solo thread obtenemos la siguiente información:

Resultados:
![result1Thread](https://user-images.githubusercontent.com/49318314/90576194-15be9b80-e183-11ea-91ab-d00551083be4.png)

Como podemos ver, hace una consulta a más de 70.000 servidores:
y tiene por consumo en el servidor un aproxumado al 20% - 25%.

![jvm1Thread](https://user-images.githubusercontent.com/49318314/90576190-15260500-e183-11ea-9296-6fc9f98e28e5.png)

###As many threads as processing cores (have the program determine this using the Runtime API).

Para el uso de la cantidad de procesadores, tenemos el método ```getProcessors()```:

![4ProcessorsResult](https://user-images.githubusercontent.com/49318314/90576183-148d6e80-e183-11ea-965a-7176ad21ede5.png)

y como resultado en la consulta podemos encontrar la siguiente información
![4ProcessorsJMV](https://user-images.githubusercontent.com/49318314/90576182-13f4d800-e183-11ea-80ae-d758800a96fb.png)

###As many threads as twice the number of processing cores.
AL momento de duplicar la cantidad de hilos por procesador obtenemos la siguiente información.

![4ProcessorsX2TJMV](https://user-images.githubusercontent.com/49318314/90576184-148d6e80-e183-11ea-8afb-cc6ed801b4d5.png)

###50 threads
Al hacer la consulta con una cantidad de 50 threads, podemos ver que hace la búsqueda a una cantidad de 49.000 servidores.

![result2Thread](https://user-images.githubusercontent.com/49318314/90576195-16573200-e183-11ea-89dd-30f8c15f314b.png)

![jvm2Thread](https://user-images.githubusercontent.com/49318314/90576191-15260500-e183-11ea-9928-86a778fb4880.png)

###100 threads

Al usar 100 threads, la cantidad de consultas vuelve a subir a 60.000 servidores.

![result3Thread](https://user-images.githubusercontent.com/49318314/90576180-135c4180-e183-11ea-9dda-26c31745ea11.png)

![jvm3Thread](https://user-images.githubusercontent.com/49318314/90576193-15be9b80-e183-11ea-9916-ea40bb5a5041.png)

###Conclusión

Como conclusión podemos ver que un aumento en la cantidad de hilos que procesan la información (Sin saturar el procesador).

<img width="195" alt="data" src="https://user-images.githubusercontent.com/49318314/90576187-148d6e80-e183-11ea-91b0-e27da54621bd.png">

![graphic](https://user-images.githubusercontent.com/49318314/90576189-15260500-e183-11ea-9956-6ace78205149.png)

Ahora bien, para 200 o 500 el rendimiento para este programa sigue aumentando aunque se plantea que no, como ejemplo podemos ver las siguientes ejecuciones con 200 Threads, 500 threads y 700 threads, en donde el rendimiento sigue aumentando en tiempo de respuesta:

**200 Threads**

<img width="466" alt="200T" src="https://user-images.githubusercontent.com/49318314/90580891-92f00d80-e18f-11ea-968e-b5db6794f1d6.png">

**500 Threads**

<img width="468" alt="500T" src="https://user-images.githubusercontent.com/49318314/90580892-9388a400-e18f-11ea-9924-b6548dd5664a.png">

A partir de este caso el rendimiento se empieza a ver comprometido con la cantidad de hilos.
**1000 Threads**
<img width="460" alt="1000T" src="https://user-images.githubusercontent.com/49318314/90580894-9388a400-e18f-11ea-926a-224abf51e3f0.png">
**2000 Threads**
<img width="462" alt="2000T" src="https://user-images.githubusercontent.com/49318314/90580895-9388a400-e18f-11ea-8a02-43bb1758a9db.png">
**10.000 Threads**
<img width="470" alt="10000T" src="https://user-images.githubusercontent.com/49318314/90580896-94213a80-e18f-11ea-9254-de81d81cfc99.png">
**40.000 Threads**
<img width="455" alt="40000T" src="https://user-images.githubusercontent.com/49318314/90580897-94213a80-e18f-11ea-84a2-77616c152368.png">


Finalmente poseemos esta tabla, donde presentamos los valores y como el rendimiento empieza a perder tiempo de respuesta a partir de la cantidad de hilos.



<img width="169" alt="data2" src="https://user-images.githubusercontent.com/49318314/90580887-92577700-e18f-11ea-8769-c607785c96a7.png">



<img width="367" alt="Graphic2" src="https://user-images.githubusercontent.com/49318314/90580889-92f00d80-e18f-11ea-9265-554e00dc8c0f.png">



Es posible asumir que este limite de procesamiento se vea influenciado a partir de los componentes de hardware.
- También, pueden presentarse retrasos a partir de la comunicación que posee.


# FINALIZACIÓN DEL LABORATORIO
## Getting Started

Principalmente se recomienda clonar el repositorio a su computadora, como opción puede realizarlo por medio del siguiente comando:

``` git clone https://github.com/JuanchoGarciaG/ARSW_2021_LAB_01.git```

La construcción del proyecto se ha realizado por medio de **MAVEN**, es por este motivo que puede ser necesario tener la herramienta en su dispositivo.

Para compilar el proyecto, ejecute el siguiente comando:

```mvn compile```

posteriormente puede hacer el empaquetado:

``` mvn package```

para verificar el correcto funcionamiento de las librerias es recomendable ejecutar las pruebas presentes en estos,
se pueden ejecutar desde el IDE de preferencia o desde comandos MVN tales como

```mvn surefire:test```

Con el fin de correr el programa ejecute la siguiente instrucción:
``` java -cp target\PiDigits-1.0-SNAPSHOT.jar edu.eci.arsw.threads.CountThreadsMain ```


## Prerequisitos.

Es necesario/recomendable que posea las siguientes herramientas:

- git instalado en su computador.
- Maven configurado en sus **Variables de Entorno**.
- Interprete de lenguaje de programacion **JAVA** (Eclipse, netbeans, Intellij, etc.)

si necesita instalar algunos de los servicios mencionados puede encontrarlos aquí:

- **Git** puede descargarlo [aqui.](https://git-scm.com/downloads)

- **Maven** puede descargarlo [aqui.](https://maven.apache.org/download.cgi)

- **IntelliJ** puede descargarlo [aqui.](https://www.jetbrains.com/es-es/idea/download/)



## Built With

* [IntelliJ](https://www.jetbrains.com/es-es/idea/) - The develop enviroment
* [Maven](https://maven.apache.org/) - Dependency Management
* [JUnit](https://junit.org/junit5/) - Used to generate Unitary Test


## Authors

* **Juan Carlos García** - *Finalización Laboratorio* - [JuanchoGarciaG](https://github.com/JuanchoGarciaG) - [IJuanchoG](https://github.com/IJuanchoG)

## License

Este proyecto es de libre uso y distribución, para más detalles vea el archivo [LICENSE.md](LICENSE.md).


