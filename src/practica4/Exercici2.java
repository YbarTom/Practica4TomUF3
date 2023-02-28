package practica4;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Samuel
 */
class Cliente {

    int codi;
    String nom;
    String cognoms;
    int DiaNaixement;
    int MesNaixement;
    int AnyNaixement;
    String AdrecaPostal;
    String mail;
    boolean VIP;
}

class Index {

    int codi;
    long posicio;
    boolean esborrat;
}

public class Exercici2 {

    /*Fes un programa que permeti gestionar el fitxer de clients amb les següents
operacions:
a) Alta d’un client (registrar un client que no existia abans al fitxer)
b) Consulta d’un client per posició
c) Consulta d’un client per codi
d) Modificar un client
e) Esborrar un client
f) Llistat de tots els clients*/
    public static final String NOM_FTX_CLIENTS_BIN = "./clients.dat";
    public static final String COPIA = "./clients_copia.dat";  
    public static final String NOM_FTX_CLIENTS_IDXPOS = "./clientes.idx_pos";
    public static Scanner scan = new Scanner(System.in);
    public static final int TAMANY_INDEX = 13;//8 long+4 int+1 boolean
  
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Menu();
    }

    /**
     * Menu amb switch que crida a les funcions respectives
     */
    private static void Menu() {
        int opcion = MostrarMenu();
        while (opcion != 7) {
            switch (opcion) {
                case 1:
                    GrabarClientesBinario();
                    break;
                case 2:
                    LeerClientesPosicion();
                    break;
                case 3:
                    LeerClientesCodigoRaf();
                    break;
                case 4:
                    ModificarClientes();
                    break;
                case 5:
                    BorrarClientes();
                    break;
                case 6:
                    LeerClientesBinario();
                    break;
            }
            opcion = MostrarMenu();
        }
    }

    /**
     * Funcio que mostra el menu
     *
     * @return l'opcio seleccionada
     */
    public static int MostrarMenu() {
        int opcion;

        System.out.println("Que vols fer?");
        System.out.println("1-Donar d'alta un client");
        System.out.println("2-Consultar un client per posicio");
        System.out.println("3-Consultar un client per codi");
        System.out.println("4-Modificar un client");
        System.out.println("5-Esborrar un client");
        System.out.println("6-Llistat de tots els clients");
        System.out.println("7-Sortir");

        opcion = scan.nextInt();
        scan.nextLine();

        return opcion;
    }

    /**
     * Funcio que obre el fitxer
     *
     * @param nomFichero nom del fitxer a obrir
     * @param crear true per si no existeix crearlo o false per no crearlo
     * @return el fitxer
     */
    public static File AbrirFichero(String nomFichero, boolean crear) {
        File result = null;

        result = new File(nomFichero);

        if (!result.exists()) {
            if (crear) {
                try {
                    result.createNewFile();
                } catch (IOException ex) {
                    Logger.getLogger(Exercici2.class.getName()).log(Level.SEVERE, null, ex);
                    result = null;
                }
            } else {
                result = null;
            }
        }

        return result;
    }

    public static DataInputStream AbrirFicheroLecturaBinario(String nomFichero, boolean crear) {
        DataInputStream dis = null;
        File f = AbrirFichero(nomFichero, crear);

        if (f != null) {
            // Declarar el writer para poder escribir en el fichero¡
            FileInputStream reader;
            try {
                reader = new FileInputStream(f);
                // PrintWriter para poder escribir más comodamente
                dis = new DataInputStream(reader);
            } catch (IOException ex) {
                Logger.getLogger(Exercici2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return dis;
    }

    public static DataOutputStream AbrirFicheroEscrituraBinario(String nomFichero, boolean crear, boolean blnAnyadir) {
        DataOutputStream dos = null;
        File f = AbrirFichero(nomFichero, crear);

        if (f != null) {
            // Declarar el writer para poder escribir en el fichero¡
            FileOutputStream writer;
            try {
                writer = new FileOutputStream(f, blnAnyadir);
                // PrintWriter para poder escribir más comodamente
                dos = new DataOutputStream(writer);
            } catch (IOException ex) {
                Logger.getLogger(Exercici2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return dos;
    }

    /**
     * Funcio que demana les dades del client
     *
     * @return el client
     */
    public static Cliente PedirDatosCliente() {
        Cliente c = new Cliente();
        System.out.print("Codi: ");
        c.codi = scan.nextInt();
        scan.nextLine();
        if (c.codi != 0) {
            System.out.print("Nom: ");
            c.nom = scan.nextLine();
            System.out.print("Cognoms: ");
            c.cognoms = scan.nextLine();
            System.out.print("DiaNaixement: ");
            c.DiaNaixement = scan.nextInt();
            System.out.print("MesNaixement: ");
            c.MesNaixement = scan.nextInt();
            System.out.print("AnyNaixement: ");
            c.AnyNaixement = scan.nextInt();
            scan.nextLine();
            System.out.print("AdresaPostal: ");
            c.AdrecaPostal = scan.nextLine();
            System.out.print("e-Mail: ");
            c.mail = scan.nextLine();
            System.out.print("VIP: ");
            c.VIP = scan.nextBoolean();
        } else {
            c = null;
        }
        return c;
    }

    public static void GrabarClientesBinario() {
        File f = AbrirFichero(NOM_FTX_CLIENTS_BIN, true);
        DataOutputStream dos = AbrirFicheroEscrituraBinario(NOM_FTX_CLIENTS_BIN, true, true);

        Cliente cli = PedirDatosCliente();
        GrabarDatosClienteBinario(dos, cli, f);

        CerrarFicheroBinarioOutput(dos);
        System.out.println("Client donat d'alta correctament");

    }

    public static void GrabarDatosClienteBinario(DataOutputStream dos, Cliente cli, File f) {

        try {
            GrabarIndiceClientePosicion(f.length(), cli.codi);//Grabamos codi y posicion en el indice

            dos.writeInt(cli.codi);
            dos.writeUTF(cli.nom);
            dos.writeUTF(cli.cognoms);
            dos.writeInt(cli.DiaNaixement);
            dos.writeInt(cli.MesNaixement);
            dos.writeInt(cli.AnyNaixement);
            dos.writeUTF(cli.AdrecaPostal);
            dos.writeUTF(cli.mail);
            dos.writeBoolean(cli.VIP);
        } catch (IOException ex) {
            Logger.getLogger(Exercici2.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    public static void GrabarDatosClienteBinarioRaf(RandomAccessFile rafClient,Cliente cli, File f) {

        try {            

            rafClient.writeInt(cli.codi);
            rafClient.writeUTF(cli.nom);
            rafClient.writeUTF(cli.cognoms);
            rafClient.writeInt(cli.DiaNaixement);
            rafClient.writeInt(cli.MesNaixement);
            rafClient.writeInt(cli.AnyNaixement);
            rafClient.writeUTF(cli.AdrecaPostal);
            rafClient.writeUTF(cli.mail);
            rafClient.writeBoolean(cli.VIP);
        } catch (IOException ex) {
            Logger.getLogger(Exercici2.class.getName()).log(Level.SEVERE, null, ex);
        }
       

    }

    /**
     * Funcio per guardar a un index i el client Index el codi,la posicio d'un
     * client y si esta esborrat
     *
     * @param posicio Posicio en bytes abans de grabar el client
     * @param codi Codi del client per guardar a l'index
     */
    public static void GrabarIndiceClientePosicion(long posicio, int codi) {

        File f = AbrirFichero(NOM_FTX_CLIENTS_IDXPOS, true);
        DataOutputStream dos = AbrirFicheroEscrituraBinario(NOM_FTX_CLIENTS_IDXPOS, true, true);
        Index i = new Index();

        /*Guardem la posicio, el codi i si el boolean esta esborrat o no(iniciem a false perque el creem),
        a la clase client*/        
        i.posicio = posicio;
        i.codi = codi;
        i.esborrat = false;

        //Grabem les dades al fitxer Index
        try {            
            dos.writeLong(i.posicio);
            dos.writeInt(i.codi);
            dos.writeBoolean(i.esborrat);
        } catch (IOException ex) {
            Logger.getLogger(Exercici2.class.getName()).log(Level.SEVERE, null, ex);
        }

        CerrarFicheroBinarioOutput(dos);
    }   

    /**
     * Funcio per a tancar el fitxer amb el data input
     *
     * @param dis dataInputStream
     */
    public static void CerrarFicheroBinarioInput(DataInputStream dis) {
        try {
            dis.close();
        } catch (IOException ex) {
            Logger.getLogger(Exercici2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Funcio per a tancar el fixter amb el data output
     *
     * @param dos DataOutputStream
     */
    public static void CerrarFicheroBinarioOutput(DataOutputStream dos) {

        try {
            dos.flush();
            dos.close();
        } catch (IOException ex) {
            Logger.getLogger(Exercici2.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void EscribirDatosCliente(Cliente client) {
        System.out.println("Codi: " + client.codi);
        System.out.println("Nom: " + client.nom);
        System.out.println("Cognoms: " + client.cognoms);
        System.out.println("Dia Naixement: " + client.DiaNaixement);
        System.out.println("Mes Naixement: " + client.MesNaixement);
        System.out.println("Any Naixement: " + client.AnyNaixement);
        System.out.println("Adresa: " + client.AdrecaPostal);
        System.out.println("e-mail: " + client.mail);
        System.out.println("VIP: " + client.VIP);
    }

    /**
     * Funcio que llegeix les dades del client amb randomAccesFile, ja que el
     * client es una clase
     *
     * @param raf RandomAccesFile
     * @return retorna el client
     */
    public static Cliente LeerDatosClienteBinarioRaf(RandomAccessFile raf) {
        Cliente cli = new Cliente();

        try {
            cli.codi = raf.readInt();
            cli.nom = raf.readUTF();
            cli.cognoms = raf.readUTF();
            cli.DiaNaixement = raf.readInt();
            cli.MesNaixement = raf.readInt();
            cli.AnyNaixement = raf.readInt();
            cli.AdrecaPostal = raf.readUTF();
            cli.mail = raf.readUTF();
            cli.VIP = raf.readBoolean();

        } catch (IOException ex) {
            cli = null;
        }
        return cli;
    }

    /**
     * Funcio que llegeix les dades del index amb DataInputStream, ja que el
     * client es una clase
     *
     * @param dis DataInputStream
     * @return retorna la clase index
     */
    public static Index LeerDatosIndiceBinario(DataInputStream dis) {
        Index i = new Index();

        try {
            i.posicio = dis.readLong();
            i.codi = dis.readInt();
            i.esborrat = dis.readBoolean();

        } catch (IOException ex) {
            i = null;
        }
        return i;
    }

    /**
     * Funcio que llegeix les dades del index amb randomAccesFile, ja que el
     * client es una clase
     *
     * @param dis RandomAccesFile
     * @return retorna la clase index
     */
    public static Index LeerDatosIndiceBinarioRaf(RandomAccessFile raf) {
        Index i = new Index();

        try {
            i.posicio = raf.readLong();
            i.codi = raf.readInt();
            i.esborrat = raf.readBoolean();

        } catch (IOException ex) {
            i = null;
        }
        return i;
    }

    public static Cliente LeerDatosClienteBinario(DataInputStream dis) {
        Cliente cli = new Cliente();

        try {
            cli.codi = dis.readInt();
            cli.nom = dis.readUTF();
            cli.cognoms = dis.readUTF();
            cli.DiaNaixement = dis.readInt();
            cli.MesNaixement = dis.readInt();
            cli.AnyNaixement = dis.readInt();
            cli.AdrecaPostal = dis.readUTF();
            cli.mail = dis.readUTF();
            cli.VIP = dis.readBoolean();

        } catch (IOException ex) {
            cli = null;
        }
        return cli;
    }
    
    
    public static void LeerClientesBinario() {
        DataInputStream dis = AbrirFicheroLecturaBinario(NOM_FTX_CLIENTS_BIN, true);
        Cliente cli = LeerDatosClienteBinario(dis);
        
        DataInputStream disindex = AbrirFicheroLecturaBinario(NOM_FTX_CLIENTS_IDXPOS, true);
        Index i = LeerDatosIndiceBinario(disindex);
        
        while (cli != null && i !=null) {
            if(i.esborrat == false){
            EscribirDatosCliente(cli);            
            }
            cli = LeerDatosClienteBinario(dis);
            i = LeerDatosIndiceBinario(disindex);
        }

        CerrarFicheroBinarioInput(dis);
    }

    /**
     * Funcio que va llegint els clients, i imprimeix el client de la posicio
     * demanada.
     */
    public static void LeerClientesPosicion() {
        try {
            System.out.print("Introdueix el numero de registre al que vols accedir: ");
            int registre = scan.nextInt();

            //per calcular la posicio on esta el registre al fitxer index, es fa la formula
            //(numeroregistre-1)*tamanydebytesqueocupaelregistre 
            long posicio_index = (registre - 1) * TAMANY_INDEX;

            RandomAccessFile raf = new RandomAccessFile(NOM_FTX_CLIENTS_IDXPOS, "r");
            raf.seek(posicio_index);

            long posicio_dades = raf.readLong();
            raf.close();

            RandomAccessFile rafClient = new RandomAccessFile(NOM_FTX_CLIENTS_BIN, "r");
            rafClient.seek(posicio_dades);

            Cliente cli = LeerDatosClienteBinarioRaf(rafClient);
            EscribirDatosCliente(cli);
            rafClient.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Exercici2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Exercici2.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Funcio que va llegint els clients, i imprimeix el client del codi
     * demanat.
     */
    public static void LeerClientesCodigo() {

        System.out.print("Introdueix el codi del client: ");
        int codiBuscar = scan.nextInt();
        long posicioBuscar = 0;

        // Creem l'enllaç amb el fitxer del index al disc per llegir
        DataInputStream dis = AbrirFicheroLecturaBinario(NOM_FTX_CLIENTS_IDXPOS, true);

        Index i = LeerDatosIndiceBinario(dis);//Anem llegint la clase Index fins que trobi el codi a buscar
        while (i != null && i.codi != codiBuscar) {
            i = LeerDatosIndiceBinario(dis);
        }

        if (i != null && i.codi == codiBuscar) {//Cuan el trobi el guardem a la variable posicioBuscar
            posicioBuscar = i.posicio;
        }
        CerrarFicheroBinarioInput(dis);

        try {
            RandomAccessFile rafClient = new RandomAccessFile(NOM_FTX_CLIENTS_BIN, "r");
            rafClient.seek(posicioBuscar);

            Cliente cli = LeerDatosClienteBinarioRaf(rafClient);
            EscribirDatosCliente(cli);
            rafClient.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Exercici2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Exercici2.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    /**
     * Funcio que va llegint els clients, i imprimeix el client del codi
     * demanat.
     */
    public static void LeerClientesCodigoModificar() {
        File f = AbrirFichero(NOM_FTX_CLIENTS_BIN, true);
        System.out.print("Introdueix el codi del client: ");
        int codiBuscar = scan.nextInt();
        long posicioBuscar = 0;

        // Creem l'enllaç amb el fitxer del index al disc per llegir
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile (NOM_FTX_CLIENTS_IDXPOS, "rw");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Exercici2.class.getName()).log(Level.SEVERE, null, ex);
        }

        Index i = LeerDatosIndiceBinarioRaf(raf);//Anem llegint la clase Index fins que trobi el codi a buscar
        while (i != null && i.codi != codiBuscar) {
            i = LeerDatosIndiceBinarioRaf(raf);
        }       
        
        RandomAccessFile rafClient=null;
        try {
            rafClient = new RandomAccessFile(NOM_FTX_CLIENTS_BIN, "rw");
            posicioBuscar =rafClient.length();
            rafClient.seek(posicioBuscar);
        } catch (IOException ex) {
            Logger.getLogger(Exercici2.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Cliente cli = PedirDatosCliente();
        i.posicio= posicioBuscar;
        i.codi = cli.codi;
        i.esborrat = false;
        
          //Grabem les dades al fitxer Index
        try {            
            raf.writeLong(i.posicio);
            raf.writeInt(i.codi);
            raf.writeBoolean(i.esborrat);
        } catch (IOException ex) {
            Logger.getLogger(Exercici2.class.getName()).log(Level.SEVERE, null, ex);
        }
        GrabarDatosClienteBinarioRaf(rafClient, cli, f);
        
       
        
    }

    /**
     * Funcio que imprimeix el client que indiques, el busca de forma directa
     * amb el fitxer index
     */
    public static void LeerClientesCodigoRaf() {

        System.out.print("Introdueix el codi del client al que vols accedir: ");
        int codiBuscar = scan.nextInt();
        long posicioBuscar = 0;

        try {
            RandomAccessFile raf = new RandomAccessFile(NOM_FTX_CLIENTS_IDXPOS, "r");

            Index i = LeerDatosIndiceBinarioRaf(raf);//Anem llegint la clase Index fins que trobi el codi a buscar
            while (i != null && i.codi != codiBuscar) {
                i = LeerDatosIndiceBinarioRaf(raf);
            }

            if (i != null && i.codi == codiBuscar) {//Cuan el trobi el guardem a la variable posicioBuscar
                posicioBuscar = i.posicio;
            }
            raf.close();

            RandomAccessFile rafClient = new RandomAccessFile(NOM_FTX_CLIENTS_BIN, "r");
            rafClient.seek(posicioBuscar);

            Cliente cli = LeerDatosClienteBinarioRaf(rafClient);
            EscribirDatosCliente(cli);
            rafClient.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Exercici2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Exercici2.class.getName()).log(Level.SEVERE, null, ex);
        }

    }    

    /**
     * Funcio que demana el codi del client a modificar, el borra i et demana
     * les dades
     */
    public static void ModificarClientes() {
        File f = AbrirFichero(NOM_FTX_CLIENTS_BIN, true);
        System.out.print("Introdueix el codi del client a modificar: ");
        int codigoModificar = scan.nextInt();
       

        GrabarClientesBinario();

    }

    /**
     * Funcio que borra el client, la funcio busca el client per el seu codi.
     */
    public static void BorrarClientes() {
        File f = AbrirFichero(NOM_FTX_CLIENTS_BIN, true);
        System.out.print("Introdueix el codi del client a esborrar: ");
        int codigoBorrar = scan.nextInt();

        LeerClientesCodigo();
        MarcarBorrado(codigoBorrar);

    }

    /**
     * Funcio que marca amb true el i.esborrat si es vol borrar
     */
    public static void MarcarBorrado(int codigoBorrar) {
        try {
            RandomAccessFile raf = new RandomAccessFile(NOM_FTX_CLIENTS_IDXPOS, "rw");

            Index i = LeerDatosIndiceBinarioRaf(raf);//Anem llegint la clase Index fins que trobi el codi a buscar
            while (i != null && i.codi != codigoBorrar) {
                i = LeerDatosIndiceBinarioRaf(raf);
            }

            if (i != null && i.codi == codigoBorrar) {//Cuan el trobi marquem el i.esborrat com a true
                i.esborrat = true;
                raf.seek(raf.getFilePointer()-1);
                raf.writeBoolean(i.esborrat);
            }
            raf.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Exercici2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Exercici2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Funcio per a borrar el fitxer
     *
     * @param filename nom del fitxer
     */
    public static void BorrarFichero(String filename) {
        File f = new File(filename);
        f.delete();
    }

    /**
     * Funcio per reanomenar el fitxer
     *
     * @param filename_origen primer nom
     * @param filename_final nom a cambiar
     */
    public static void RenombrarFichero(String filename_origen, String filename_final) {
        File f = new File(filename_origen);
        File f2 = new File(filename_final);
        f.renameTo(f2);
    }
}
