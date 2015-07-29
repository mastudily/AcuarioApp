/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acuario.clases;

import acuario.services.Constante;
import acuario.services.Cuentacontable;
import acuario.services.Insumoempresa;
import acuario.services.Perfil;
import acuario.services.Programa;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import javafx.scene.control.ComboBox;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 *
 * @author Marcelo
 */
public class Util {

    public static List getSelectConstante(List<Constante> entities, boolean selectOne) {
        int size = selectOne ? entities.size() + 1 : entities.size();
        List<SelectItem> items = new ArrayList(size);
        if (selectOne) {
            items.add(new SelectItem("", "---"));
        }
        for (Constante x : entities) {
            items.add(new SelectItem(x.getConstantetipo(), x.getConstantedescripcion()));
        }
        return items;
    }

    public static List getSelectCuentacontable(List<Cuentacontable> entities, boolean selectOne) {
        int size = selectOne ? entities.size() + 1 : entities.size();
        List<SelectItem> items = new ArrayList(size);
        if (selectOne) {
            items.add(new SelectItem("", "---"));
        }
        for (Cuentacontable x : entities) {
            items.add(new SelectItem(x.getCuentacontablecodigo(),x.getCuentacontablenombre()));
        }
        return items;
    }

    public static List getSelectInsumo(List<Insumoempresa> entities, boolean selectOne) {
        int size = selectOne ? entities.size() + 1 : entities.size();
        List<SelectItem> items = new ArrayList(size);
        if (selectOne) {
            items.add(new SelectItem("", "---"));
        }
        for (Insumoempresa x : entities) {
            items.add(new SelectItem(x.getInsumo().getInsumoid(), x.getInsumo().getInsumonombre()));
        }
        return items;
    }


    public static List getSelectPerfil(List<Perfil> entities, boolean selectOne) {
        int size = selectOne ? entities.size() + 1 : entities.size();
        List<SelectItem> items = new ArrayList(size);
        if (selectOne) {
            items.add(new SelectItem("", "---"));
        }
        for (Perfil x : entities) {
            items.add(new SelectItem(x.getPerfilid().toString(), x.getPerfilnombre()));
        }
        return items;
    }

    public static List getSelectPrograma(List<Programa> entities, boolean selectOne) {
        int size = selectOne ? entities.size() + 1 : entities.size();
        List<SelectItem> items = new ArrayList(size);
        if (selectOne) {
            items.add(new SelectItem("", "---"));
        }
        for (Programa x : entities) {
            items.add(new SelectItem(x.getProgramaid().toString(), x.getProgramanombre()));
        }
        return items;
    }
    
    public static int indiceSeleccionado(List lista, String dato) {
        int indice = 0;
        if (lista != null && dato != null) {
            for (Iterator iterator = lista.iterator(); iterator.hasNext();) {
                SelectItem next = (SelectItem) iterator.next();
                String idLista = (String) next.getId();
                if (idLista.equals(dato)) {
                    return indice;
                }
                indice++;
            }
        }
        return indice;
    }

    public static int indiceNumericoSeleccionado(List lista, Integer dato) {
        int indice = 0;
        for (Iterator iterator = lista.iterator(); iterator.hasNext();) {
            SelectItem next = (SelectItem) iterator.next();
            if (!"---".equals(next.getDescription())) {
                Integer idLista = (Integer) next.getId();
                if (idLista.equals(dato)) {
                    return indice;
                }
            }
            indice++;
        }
        return indice;
    }

    public static Integer codigoSeleccionado(ComboBox combo) {
        SelectItem itm = (SelectItem) combo.getSelectionModel().getSelectedItem();
        Integer codret = 0;
        if (itm != null) {
            if (itm.getId() != "") {
                codret = Integer.parseInt(itm.getId().toString());
            }
        }
        return codret;
    }

    public static String stringSeleccionado(ComboBox combo) {
        SelectItem itm = (SelectItem) combo.getSelectionModel().getSelectedItem();
        String codret = "";
        if (itm != null) {
            if (itm.getId() != "") {
                codret = (String) itm.getId();
            }
        }
        return codret;
    }

    public static String descripcionSeleccionado(ComboBox combo) {
        SelectItem itm = (SelectItem) combo.getSelectionModel().getSelectedItem();
        String des = "";
        if (itm != null) {
            if (itm.getId() != "") {
                des = itm.getDescription();
            }
        }
        return des;
    }

    public static XMLGregorianCalendar getXMLCalendar(String strDate) throws Exception {
        Calendar sDate = Calendar.getInstance();
        DatatypeFactory dtf = DatatypeFactory.newInstance();
        XMLGregorianCalendar calendar = null;

        // Dates (CCYY-MM-DD)
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-M-d");

        if (strDate != null) {
            sDate.setTime(DATE_FORMAT.parse(strDate));
            calendar = dtf.newXMLGregorianCalendar();
            calendar.setYear(sDate.get(Calendar.YEAR));
            calendar.setDay(sDate.get(Calendar.DAY_OF_MONTH));
            calendar.setMonth(sDate.get(Calendar.MONTH) + 1);
            calendar.setTime(0, 0, 0, 0);
        }
        return calendar;
    }

    public static String uploadFile(String inputFile, String path, String url) {
        String fileName = null;
        try {
            HttpPost httpPost = new HttpPost(new URL(url + "copyFileServlet?path=" + path));
            httpPost.setFileNames(new String[]{inputFile});
            httpPost.post();
            System.out.println("=======");
            System.out.println(httpPost.getOutput());
            String aux = inputFile.substring(inputFile.lastIndexOf("\\") + 1);
            fileName = url + path + "/" + aux;
        } catch (MalformedURLException ex) {
            System.err.println("MalformedURLException:" + ex);
        }
        return fileName;
    }

    /*   public static List getSelectEmpresas(List<SifvEmpresas> entities, boolean selectOne) {
     int size = selectOne ? entities.size() + 1 : entities.size();
     List<SelectItem> items = new ArrayList(size);
     if (selectOne) {
     items.add(new SelectItem("", "---"));
     }
     for (SifvEmpresas x : entities) {
     items.add(new SelectItem(x.getCodigoEmpresa(), x.getNombre()));
     }
     return items;
     }

     public static List getSelectSucursales(List<SifvSucursales> entities, boolean selectOne) {
     int size = selectOne ? entities.size() + 1 : entities.size();
     List<SelectItem> items = new ArrayList(size);
     if (selectOne) {
     items.add(new SelectItem("", "---"));
     }
     for (SifvSucursales x : entities) {
     items.add(new SelectItem(x.getCodigoSucursal(), x.getDescripcion()));
     }        
     return items;
     }

     public static List getSelectAgencias(List<SifvAgencias> entities, boolean selectOne) {
     int size = selectOne ? entities.size() + 1 : entities.size();
     List<SelectItem> items = new ArrayList(size);
     if (selectOne) {
     items.add(new SelectItem("", "---"));
     }
     for (SifvAgencias x : entities) {
     items.add(new SelectItem(x.getCodigoAgencia(), x.getDescripcion()));
     }
     return items;
     }

     public static List getSelectUsuarios(List<SifvUsuariosSistema> entities, boolean selectOne) {
     int size = selectOne ? entities.size() + 1 : entities.size();
     List<SelectItem> items = new ArrayList(size);
     if (selectOne) {
     items.add(new SelectItem("", "---"));
     }
     for (SifvUsuariosSistema x : entities) {
     items.add(new SelectItem(x.getCodigoUsuario(), x.getUsuNombres() + " " + x.getUsuApellidos()));
     }
     return items;
     }

     public static List getSelectMonedas(List<PMoneda> entities, boolean selectOne) {
     int size = selectOne ? entities.size() + 1 : entities.size();
     List<SelectItem> items = new ArrayList(size);
     if (selectOne) {
     items.add(new SelectItem("", "---"));
     }
     for (PMoneda x : entities) {
     items.add(new SelectItem(x.getCodigoMoneda(), x.getDescripcionMoneda()));
     }
     return items;
     }

     public static List getSelectTipoPersona(List<PTipoPersona> entities, boolean selectOne) {
     int size = selectOne ? entities.size() + 1 : entities.size();
     List<SelectItem> items = new ArrayList(size);
     if (selectOne) {
     items.add(new SelectItem("", "---"));
     }
     for (PTipoPersona x : entities) {
     items.add(new SelectItem(x.getCodigoTipoCli(), x.getDescripcion()));
     }
     return items;
     }

     public static List getSelectProductos(List<PProductos> entities, boolean selectOne) {
     int size = selectOne ? entities.size() + 1 : entities.size();
     List<SelectItem> items = new ArrayList(size);
     if (selectOne) {
     items.add(new SelectItem("", "---"));
     }
     for (PProductos x : entities) {
     items.add(new SelectItem(x.getCodigoProducto(), x.getDescripcion()));
     }
     return items;
     }

     public static List getSelectTransacciones(List<PTransacciones> entities, boolean selectOne) {
     int size = selectOne ? entities.size() + 1 : entities.size();
     List<SelectItem> items = new ArrayList(size);
     if (selectOne) {
     items.add(new SelectItem("", "---"));
     }
     for (PTransacciones x : entities) {
     items.add(new SelectItem(x.getCodigoTransaccion(), x.getDescripcion()));
     }
     return items;
     }

     public static List getSelectConceptosTransaccion(List<PConceptosTransaccion> entities, boolean selectOne) {
     int size = selectOne ? entities.size() + 1 : entities.size();
     List<SelectItem> items = new ArrayList(size);
     if (selectOne) {
     items.add(new SelectItem("", "---"));
     }
     for (PConceptosTransaccion x : entities) {
     items.add(new SelectItem(x.getCodigoConcepto(), x.getDescripcion()));
     }
     return items;
     }

     public static List getSelectEfectiviza(List<CodigoDescripcion> entities, boolean selectOne) {
     int size = selectOne ? entities.size() + 1 : entities.size();
     List<SelectItem> items = new ArrayList(size);
     if (selectOne) {
     items.add(new SelectItem("", "---"));
     }
     for (CodigoDescripcion x : entities) {
     items.add(new SelectItem(x.getCodigo(), x.getDescripcion()));
     }
     return items;
     }
    
     public static List getSelectBancos(List<CodigoDescripcion> entities, boolean selectOne) {
     int size = selectOne ? entities.size() + 1 : entities.size();
     List<SelectItem> items = new ArrayList(size);
     if (selectOne) {
     items.add(new SelectItem("", "---"));
     }
     for (CodigoDescripcion x : entities) {
     items.add(new SelectItem(x.getCodigo(), x.getDescripcion()));
     }
     return items;
     }

     public static short codigoSeleccionadoChoice(ChoiceBox combo) {
     SelectItem itm = (SelectItem) combo.getSelectionModel().getSelectedItem();
     short codret = 0;
     if (itm != null) {
     if (itm.getId() != "") {
     codret = Short.parseShort(itm.getId().toString());
     }
     }
     return codret;
     }

     public static String descripcionSeleccionadoChoice(ChoiceBox combo) {
     SelectItem itm = (SelectItem) combo.getSelectionModel().getSelectedItem();
     String des = "";
     if (itm != null) {
     if (itm.getId() != "") {
     des = itm.getDescription();
     }
     }
     return des;
     }*/
    public static XMLGregorianCalendar fechaSistema() {

        XMLGregorianCalendar xmlGregorianCalendar = null;
        try {
            Calendar sDate = Calendar.getInstance();
            final Date date = new Date(sDate.getTimeInMillis());

            final GregorianCalendar gregorianCalendar = (GregorianCalendar) Calendar
                    .getInstance();
            gregorianCalendar.setTime(date);

            xmlGregorianCalendar = DatatypeFactory.newInstance()
                    .newXMLGregorianCalendar(gregorianCalendar);
        } catch (final Exception e) {
        }
        return xmlGregorianCalendar;
    }

    public static XMLGregorianCalendar horaSistema() {

        XMLGregorianCalendar xmlGregorianCalendar = null;
        try {
            Calendar sDate = Calendar.getInstance();
            final Date date = new Date(sDate.getTimeInMillis());

            final GregorianCalendar gregorianCalendar = (GregorianCalendar) Calendar
                    .getInstance();
            gregorianCalendar.setTime(date);
            TimeZone utc = TimeZone.getTimeZone("UTC");
            gregorianCalendar.setTimeZone(utc);
            XMLGregorianCalendar xgc = DatatypeFactory.newInstance()
                    .newXMLGregorianCalendar(gregorianCalendar);

            xmlGregorianCalendar = DatatypeFactory.newInstance()
                    .newXMLGregorianCalendarTime(xgc.getHour(), xgc.getMinute(), xgc.getSecond(),
                            xgc.getFractionalSecond(), xgc.getTimezone());
            xmlGregorianCalendar.setYear(xgc.getYear());
            xmlGregorianCalendar.setMonth(xgc.getMonth());
            xmlGregorianCalendar.setDay(xgc.getDay());
            //xmlGregorianCalendar.setTimezone(xgc.getTimezone());
        } catch (final Exception e) {
        }
        return xmlGregorianCalendar;
    }
}
