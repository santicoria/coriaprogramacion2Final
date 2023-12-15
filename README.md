Como ejecutar:
  - Ejecutar Consul usando: docker-compose -f src/main/docker/consul.yml up
  - Ejecutar el Gateway. Ubicarse en la carpeta Gateway y ejecutarlo con: ./mvnw
  - Ejecutar el Procesador de Ordenes. Ubicarse en la carpeta ProcesadordeOrdenesV2 y ejecutarlo con: ./mvnw

Una vez corriendo los servidores se puede utilizar el servicio. El servicio escucha el puerto 8081. Algunos links utiles:
  - http://localhost:8081/api/ordenes ------> Trae las ordenes del servicio de catedra y las guarda en las base de datos local.
  - http://localhost:8081/api/ordens ------> Muestra las ordenes almacenadas en la base de datos local.
  - http://localhost:8081/api/analizar-ordenes ------> Ejecuta el analisis de ordenes.
  - http://localhost:8081/api/reportes/buscar-procesadas?cliente=201225&accion=1&fechaInicio=2023-10-04T14:00:00&fechaFin=2023-12-03T14:00:00 ------> Permite buscar las ordenes procesadas. Permite filtrar por ID de cliente, ID de accion y fecha.
  - http://localhost:8081/api/reportes/buscar-noprocesadas ------> Devuelve las ordenes que no fueron procesadas.

Funcionamiento del Servicio:
  - Obtener listado de ordenes:
      * Al recibir el Get Request, el programa llama al metodo getOrdenesCatedra del servicio GetOrdenesFromCatedraService.
      * Este metodo hace un llamado al metodo getOrdenes que hace el Get Request al servicio de catedra para traer las ordenes y luego transforma el resultado en una Lista de OrdenesDTO.
      * Al recibir la lista, el metodo getOrdenesCatedra ejecuta el return haciendo un llamado al metodo saveOrdenes. El metodo saveOrdenes se encarga de escribir en los archivos de log para luego llamar al metodo saveAll del servicio OrdenService,
        que se encarga de escribir los datos en la base de datos local.
      * Se hace un log con las ordenes obtenidas.
  - Analizar ordenes:
      * Al recibir el Get Request, el metodo analizarOrdenes de OrdenResource, ejecuta el metodo analizarOrdenes del servicio AnalizarOrdenService.
      * El metodo analizarOrdenes trae todas las ordenes no procesadas de la base de datos local y las guarda en una lista. Tambien almacena los IDs de las ordenes en una lista.
      * Luego se va pasando orden por orden al metodo analyzer. El metodo analyzer va a asegurarse de que la informacion de la orden sea correcta y va a establecer el tipo y el modo de la orden.
      * Se utiliza el servicio DataVerificationService cuyos metodos checkAccion y checkCliente verifican los valores con el servicio de catedra.
      * Una vez analizada la orden, se devuelve un valor de acuerdo a los parametros de la orden.
      * Este valor es pasado al codeHandler, quien decide que hacer con la orden de acuerdo al valor. Si el valor esta entre 1 y 4 se pasa la orden al metodo errorCodeHandler. Si el valor es 5 o 6 se llama a ejecutarOperacion. Si el valor es 7 se llama
        a programarOperacion.
      * Si el metodo es errorCodeHandler, se transforma la orden en ObjectNode y se le agrega 2 nuevos campos: "operacionExitosa" (Cuyo valor va a ser false ya que este metodo se encarga de los problemas) y "operacionObservaciones" (Da una descripcion
        del problema). Una vez agregado los campos, se devuelve el objeto con los nuevos campos añadidos.
      * Si se llama al metodo ejecutarOperacion, se le agregan los mismos campos pero con diferentes valores: "operacionExitosa" que va a ser true y "operacionObservaciones" que va a decir "Compra exitosa!" o "Venta exitosa!" de acuerdo a la operacion.
        Luego se devuelve el objeto con los nuevos campos añadidos. Este metodo hace uso del servico CompraVentaService llamando al metodo adecuado (comprarAccion o venderAccion).
      * Si el codigo del analyzer es 7, se llama al metodo programarOperacion del servicio ProgramarOperacionService.
      * El metodo programarOperacion va a llamar el metodo programPrincipioDia o programFinDia de acuerdo al valor del modo de la operacion.
      * Tanto programPrincipioDia como programFinDia funcionan similar. Llaman al metodo calculateDelayToNextExecution quien devuelve cuanto falta para ejecutar, luego toman ese valor y cuando llega el tiempo adecuado llaman al metodo ejecutarOperacion.
      * El metodo ejecutarOperacion pasa la orden al servico CompraVentaService llamando al metodo adecuado y luego transforma la orden en ObjectNode para añadirle los campos "operacionExitosa" y "operacionObservaciones".
      * Luego pasa las ordenes procesadas con sus nuevos campos al servicio ReporteOperacionesService cuyo metodo reportarOperacionACatedra reporta la operacion al servicio de catedra y reportarOperacionInterno almacena el reporte en la base de datos local.
      * Se hace un log de las ordenes programadas.
      * Una vez concluido el analisis, se reportan las operaciones que no se hayan reportado y se ejecuta el metodo cleanDb del CleanerService quien borra las ordenes ya procesadas de la base de datos local para no procesarlas dos veces. Nos quedamos con
        los reportes.
    
