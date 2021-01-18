package Source;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import Source.LogDayItem;
import Source.LogItem;
import java.text.Normalizer;

public class LogModel {

    private String path = "src/Source/log.json";
    private String c = "log.json";
    private ArrayList<String> actionIdentificators;
    private ArrayList<String> itemIdentificators;

    public LogModel() {
        // activar si se compila
        // path = c;

        actionIdentificators = new ArrayList<String>();
        itemIdentificators = new ArrayList<String>();
        getIdOfAll();
    }

    public ArrayList<LogDayItem> getAllItems() {

        ArrayList returnItems = new ArrayList<LogDayItem>();
        JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader(path)) {

            // jsonArray es todo el archivo json en forma de array
            JSONArray jsonArray = (JSONArray) parser.parse(reader);

            if (!jsonArray.isEmpty()) {

                // recorre cada instancia del array
                for (int i = 0; i < jsonArray.size(); i++) {

                    // agarra ese lugarcito donde está i
                    JSONObject json = (JSONObject) jsonArray.get(i);

                    returnItems.add(createLogDayItem(json));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return returnItems;
    }

    public String createId() {

        int id = new Random().nextInt(999999);
        String resultado;
        resultado = "" + id;

        // si no esta ya creado...
        if (actionIdentificators.contains(resultado)) {
            while (actionIdentificators.contains(resultado)) {
                int aux = new Random().nextInt(999999);
                resultado = "" + aux;
            }
        }

        return resultado;
    }

    public Item createItem(JSONObject ff) {

        String id = (String) ff.get("id");

        JSONObject info = (JSONObject) ff.get("atributos");

        String descripcion = (String) info.get("descripcion");
        String nombre = (String) info.get("nombre");

        int precioVenta = CasterObjToInt(info.get("venta"));
        int precioCompra = CasterObjToInt(info.get("compra"));
        int cantidad = CasterObjToInt(info.get("cantidad"));

        String categoria = (String) info.get("categoria");

        String fecha_compra = (String) info.get("fecha_compra");
        String fecha_venta = (String) info.get("fecha_venta");

        Item item = new Item(nombre, descripcion, precioVenta, precioCompra, cantidad);
        item.setId(id);
        item.setCategoria(categoria);
        item.setFechaCompra(fecha_compra);
        item.setFechaVenta(fecha_venta);

        return item;
    }

    public LogDayItem searchByDate(String searched_date) {
        JSONParser parser = new JSONParser();

        // array con retorno de los logs
        LogDayItem returnItem = null;

        try (Reader reader = new FileReader(path)) {

            // jsonArray es todo el archivo json en forma de array
            JSONArray jsonArray = (JSONArray) parser.parse(reader);

            if (!jsonArray.isEmpty()) {

                // recorre cada instancia del array
                for (int i = 0; i < jsonArray.size(); i++) {

                    // agarra ese lugarcito donde está i
                    JSONObject json = (JSONObject) jsonArray.get(i);

                    String fecha = json.keySet().toString();
                    fecha = clearString(fecha);

                    if (searched_date.equals(fecha)) {
                        returnItem = createLogDayItem(json);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return returnItem;
    }

    public ArrayList<LogDayItem> searchByItemName(String searched_name) {
        searched_name = searched_name.toUpperCase();

        JSONParser parser = new JSONParser();

        // array con retorno de los logs
        ArrayList<LogDayItem> returnArray = new ArrayList<LogDayItem>();

        try (Reader reader = new FileReader(path)) {

            // jsonArray es todo el archivo json en forma de array
            JSONArray jsonArray = (JSONArray) parser.parse(reader);

            if (!jsonArray.isEmpty()) {

                // recorre cada instancia del array
                for (int i = 0; i < jsonArray.size(); i++) {

                    // agarra ese lugarcito donde está i
                    JSONObject json = (JSONObject) jsonArray.get(i);

                    String fecha = json.keySet().toString();
                    fecha = clearString(fecha);

                    LogDayItem returnDayItem = new LogDayItem(fecha); // iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii

                    // agarro las actions que pasaron ese dia (forma de array)
                    JSONArray actionsArray = (JSONArray) json.get(fecha);

                    for (int j = 0; j < actionsArray.size(); j++) {

                        // agarro una accion entera
                        JSONObject action = (JSONObject) actionsArray.get(j);

                        // agarro su array de items
                        JSONArray items = (JSONArray) action.get("items");
                        String action_aux = (String) action.get("action");
                        String id_aux = (String) action.get("id");

                        LogItem returnLogItem = new LogItem(action_aux, id_aux);

                        for (int k = 0; k < items.size(); k++) {
                            // agarro un item entero
                            JSONObject item = (JSONObject) items.get(k);

                            // agarro la id de tal item
                            JSONObject attributes = (JSONObject) item.get("atributos");
                            String item_name = (String) attributes.get("nombre");

                            item_name = Normalizer.normalize(item_name, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]",
                                    "");

                            item_name = item_name.toUpperCase();

                            // si la id que busco es igual a la que tengo...
                            if (item_name.contains(searched_name)) {
                                Item ff = createItem(item);
                                returnLogItem.addItem(ff);
                            }
                        }
                        // si no esta vacio entonces messirve
                        if (!returnLogItem.isEmpty()) {
                            returnDayItem.addAccion(returnLogItem);
                        }
                    }
                    // si no esta vacio entonces messirve x2
                    if (!returnDayItem.isEmpty()) {
                        returnArray.add(returnDayItem);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return returnArray;
    }

    // lets comment dis sheeeeeet
    public ArrayList<LogDayItem> searchByItemId(String id) {
        JSONParser parser = new JSONParser();

        // array con retorno de los logs
        ArrayList<LogDayItem> returnArray = new ArrayList<LogDayItem>();

        try (Reader reader = new FileReader(path)) {

            // jsonArray es todo el archivo json en forma de array
            JSONArray jsonArray = (JSONArray) parser.parse(reader);

            if (!jsonArray.isEmpty()) {

                // recorre cada instancia del array
                for (int i = 0; i < jsonArray.size(); i++) {

                    // agarra ese lugarcito donde está i
                    JSONObject json = (JSONObject) jsonArray.get(i);

                    String fecha = json.keySet().toString();
                    fecha = clearString(fecha);

                    LogDayItem returnDayItem = new LogDayItem(fecha); // iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii

                    // agarro las actions que pasaron ese dia (forma de array)
                    JSONArray actionsArray = (JSONArray) json.get(fecha);

                    for (int j = 0; j < actionsArray.size(); j++) {

                        // agarro una accion entera
                        JSONObject action = (JSONObject) actionsArray.get(j);

                        // agarro su array de items
                        JSONArray items = (JSONArray) action.get("items");
                        String action_aux = (String) action.get("action");
                        String id_aux = (String) action.get("id");

                        LogItem returnLogItem = new LogItem(action_aux, id_aux);

                        for (int k = 0; k < items.size(); k++) {
                            // agarro un item entero
                            JSONObject item = (JSONObject) items.get(k);

                            // agarro la id de tal item
                            String item_id = (String) item.get("id");

                            // si la id que busco es igual a la que tengo...
                            if (id.equals(item_id)) {
                                Item ff = createItem(item);
                                returnLogItem.addItem(ff);
                            }
                        }
                        // si no esta vacio entonces messirve
                        if (!returnLogItem.isEmpty()) {
                            returnDayItem.addAccion(returnLogItem);
                        }
                    }
                    // si no esta vacio entonces messirve x2
                    if (!returnDayItem.isEmpty()) {
                        returnArray.add(returnDayItem);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return returnArray;
    }

    public void insertOperation(String action, ArrayList<Item> items) {
        String date = getCurrentDate();

        if (action.equals("SELL")) {
            for (Item item : items) {
                item.setFechaVenta(date);
            }
        }

        JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader(path)) {

            // jsonArray es todo el archivo json en forma de array
            JSONArray jsonArray = (JSONArray) parser.parse(reader);

            if (!jsonArray.isEmpty()) {

                // lo tengo que hacer string y despues parsearlo para que me tome el null
                // dios...es horrible
                String index = null;
                String fecha = null;

                // recorre cada instancia del array
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject json = (JSONObject) jsonArray.get(i);

                    fecha = json.keySet().toString();
                    fecha = clearString(fecha);

                    if (fecha.equals(date)) {
                        index = "" + i;
                        i = jsonArray.size();
                    }
                }

                if (index != null) {
                    int index_aux = Integer.parseInt(index);

                    JSONObject json = (JSONObject) jsonArray.get(index_aux);

                    JSONObject action_wrapper = new JSONObject();
                    action_wrapper.put("id", createId());
                    action_wrapper.put("action", action.toUpperCase());

                    JSONArray item_array = new JSONArray();

                    for (Item item : items) {
                        item_array.add(parseItem(item));
                    }

                    action_wrapper.put("items", item_array);

                    JSONArray actionsArray = (JSONArray) json.get(fecha);

                    actionsArray.add(action_wrapper);

                    JSONObject wrapper = new JSONObject();

                    wrapper.put(fecha, actionsArray);
                    jsonArray.remove(index_aux);
                    jsonArray.add(index_aux, wrapper);

                } else {
                    JSONObject action_wrapper = new JSONObject();
                    action_wrapper.put("id", createId());
                    action_wrapper.put("action", action.toUpperCase());

                    JSONArray item_array = new JSONArray();

                    for (Item item : items) {
                        item_array.add(parseItem(item));
                    }

                    action_wrapper.put("items", item_array);

                    JSONObject newDate = new JSONObject();
                    JSONArray actionArray = new JSONArray();

                    actionArray.add(action_wrapper);
                    newDate.put(date, actionArray);

                    jsonArray.add(0, newDate);
                }
            }
            try (FileWriter file = new FileWriter(path)) {
                file.write(jsonArray.toJSONString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // aber, comentemos esto que no entiendo un CHOTO
    public LogDayItem createLogDayItem(JSONObject ff) {

        String fecha = ff.keySet().toString();
        // del objeto le saco la fecha y se la limpio, same stuff
        fecha = clearString(fecha);

        // creo el dia en el cual pasaron todas estas cosas, con su fecha
        LogDayItem item_day_logger = new LogDayItem(fecha);

        // Aca voy a guardar las "cosas" que pasaron en el dia
        ArrayList<LogItem> logItems = new ArrayList<LogItem>();

        // agarro el array de cosas que pasaron
        JSONArray jsonActionsArray = (JSONArray) ff.get(fecha);

        // por cada cosa que paso...
        for (int i = 0; i < jsonActionsArray.size(); i++) {

            // aca agarro la "cosa" que paso
            JSONObject jsonLogger = (JSONObject) jsonActionsArray.get(i);

            // agarro la accion. que paso? compra? venta?
            String action = (String) jsonLogger.get("action");
            String id = (String) jsonLogger.get("id");

            // aca creo la "cosa"
            LogItem item_logger = new LogItem(action, id);

            // y agarro los objetos que estuvieron relacionados a la accion
            JSONArray item_array = (JSONArray) jsonLogger.get("items");

            // aca voy a guardar los objetos
            ArrayList<Item> item_list = new ArrayList<Item>();

            // por cada objeto, lo parseo y agrego
            for (int j = 0; j < item_array.size(); j++) {
                JSONObject item = (JSONObject) item_array.get(j);
                item_list.add(createItem(item));
            }

            // a la accion del dia, le agrego los items relacionados
            item_logger.setItems(item_list);

            // y finalmente al dia le agrego la "cosa"
            logItems.add(item_logger);
        }

        // agrego las "Cosas" al dia
        item_day_logger.setAcciones_del_dia(logItems);
        return item_day_logger;
    }

    public String clearString(String ff) {
        String fecha = "";
        for (int j = 0; j < ff.length(); j++) {
            if (!(j == 0 || j == ff.length() - 1)) {
                fecha += ff.charAt(j);
            }
        }
        return fecha;
    }

    public int CasterObjToInt(Object ff) {
        Object obj = ff;
        String str = obj.toString();
        int integer = Integer.parseInt(str);

        return integer;
    }

    public void getIdOfAll() {
        JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader(path)) {

            // jsonArray es todo el archivo json en forma de array
            JSONArray jsonArray = (JSONArray) parser.parse(reader);

            if (!jsonArray.isEmpty()) {

                // recorre cada instancia del array
                for (int i = 0; i < jsonArray.size(); i++) {

                    // agarra ese lugarcito donde está i
                    JSONObject json = (JSONObject) jsonArray.get(i);

                    String dirtyDate = json.keySet().toString();
                    String dayDate = clearString(dirtyDate);

                    JSONArray arrayDayActions = (JSONArray) json.get(dayDate);

                    for (int j = 0; j < arrayDayActions.size(); j++) {

                        JSONObject action = (JSONObject) arrayDayActions.get(j);

                        String actionId = (String) action.get("id");
                        actionIdentificators.add(actionId);

                        JSONArray arrayActionItems = (JSONArray) action.get("items");

                        for (int k = 0; k < arrayActionItems.size(); k++) {
                            JSONObject item = (JSONObject) arrayActionItems.get(k);

                            String item_id = (String) item.get("id");
                            itemIdentificators.add(item_id);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public JSONObject parseItem(Item ff) {
        JSONObject obj = new JSONObject();

        obj.put("id", ff.getId());

        JSONObject details = new JSONObject();
        details.put("venta", ff.getVenta());
        details.put("compra", ff.getCompra());
        details.put("nombre", ff.getNombre());
        details.put("descripcion", ff.getDescripcion());
        details.put("cantidad", ff.getCantidad());
        details.put("fecha_compra", ff.getFechaCompra());
        details.put("fecha_venta", ff.getFechaVenta());

        obj.put("atributos", details);

        return obj;
    }

    public ArrayList<String> getItemIdentificators() {
        return itemIdentificators;
    }

    public ArrayList<Item> getActionWithDate(String date, String id) {

        ArrayList<Item> returnItems = new ArrayList<Item>();

        JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader(path)) {

            // jsonArray es todo el archivo json en forma de array
            JSONArray jsonArray = (JSONArray) parser.parse(reader);

            if (!jsonArray.isEmpty()) {

                // recorre cada instancia del array
                for (int i = 0; i < jsonArray.size(); i++) {

                    // agarra ese lugarcito donde está i
                    JSONObject json = (JSONObject) jsonArray.get(i);

                    String dirtyDate = json.keySet().toString();
                    String dayDate = clearString(dirtyDate);

                    if (date.equals(dayDate)) {
                        i = jsonArray.size();

                        JSONArray arrayDayActions = (JSONArray) json.get(dayDate);

                        for (int j = 0; j < arrayDayActions.size(); j++) {

                            JSONObject action = (JSONObject) arrayDayActions.get(j);

                            String actionId = (String) action.get("id");

                            if (actionId.equals(id)) {
                                j = arrayDayActions.size();

                                JSONArray arrayActionItems = (JSONArray) action.get("items");

                                for (int k = 0; k < arrayActionItems.size(); k++) {
                                    JSONObject item = (JSONObject) arrayActionItems.get(k);
                                    returnItems.add(createItem(item));
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return returnItems;
    }

    public String getCurrentDate() {
        // nota del editor: dia, mes, año
        String pattern = "dd-MM-yyyy";
        String dateInString = new SimpleDateFormat(pattern).format(new Date());
        return dateInString;
    }
}
