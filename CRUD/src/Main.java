package CRUD.src;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.xml.transform.Source;

public class Main {
    public static void main(String[] args) throws IOException {

        Scanner userInput = new Scanner(System.in);
        String pilihanUser;
        boolean isLanjutkan = true;
        
        while (isLanjutkan){
            clearScreen();
            System.out.println("Database Gudang\n");
            System.out.println("1.\tLihat seluruh barang");
            System.out.println("2.\tCari data barang");
            System.out.println("3.\tTambah data barang");
            System.out.println("4.\tUbah data barang");
            System.out.println("5.\tHapus data barang");

            System.out.print("\n\nPilihan anda: ");
            pilihanUser = userInput.next();

            switch (pilihanUser) {
                case "1":
                    System.out.println("\n==================");
                    System.out.println("LIST SELURUH BARANG");
                    System.out.println("=====================");
                    tampilkanData();
                    break;
                case "2":
                    System.out.println("\n===========");
                    System.out.println("CARI BARANG");
                    System.out.println("============");
                    cariData();
                    break;
                case "3":
                    System.out.println("\n=================");
                    System.out.println("TAMBAH DATA BARANG");
                    System.out.println("==================");
                    tambahData();
                    tampilkanData();
                    break;
                case "4":
                    System.out.println("\n===============");
                    System.out.println("UBAH DATA BARANG");
                    System.out.println("================");
                    updateData();    
                    break;
                case "5":
                    System.out.println("\n================");
                    System.out.println("HAPUS DATA BARANG");
                    System.out.println("=================");
                    deleteData();
                    break;
                default:
                System.err.println("\nInput anda tidak ditemukan\nSilahkan pilih [1-5]");
            }
            isLanjutkan = getYesorNo("Apakah Anda ingin melanjutkan");
        }

    }

    private static void updateData() throws IOException{
        // kita ambil database original
        File database = new File("CRUD/src/database.txt");
        FileReader fileInput = new FileReader(database);
        BufferedReader bufferedInput = new BufferedReader(fileInput);

        // kita buat database sementara
        File tempDB = new File("CRUD/src/tempDB.txt");
        FileWriter fileOutput = new FileWriter(tempDB);
        BufferedWriter bufferedOutput = new BufferedWriter(fileOutput);

        // tampilkan 
        System.out.println("List Buku");
        tampilkanData();

        // ambil user input / pilihan data
        Scanner userInput = new Scanner(System.in);
        System.out.print("\nMasukan nomor barang yang akan diupdate: ");
        int updateNum = userInput.nextInt();

        // tampilkan data yang ingin diupdate

        String data = bufferedInput.readLine();
        int entryCounts = 0;

        while (data != null){
            entryCounts++;

            StringTokenizer st = new StringTokenizer(data,",");

            // tampilkan entrycounts == updateNum
            if (updateNum == entryCounts){
                System.out.println("\nData yang ingin anda update adalah:");
                System.out.println("---------------------------------------");
                System.out.println("Referensi       : " + st.nextToken());
                System.out.println("Divisi          : " + st.nextToken());
                System.out.println("Nama            : " + st.nextToken());
                System.out.println("Jenis           : " + st.nextToken());
                System.out.println("Barang          : " + st.nextToken());

                // update data

                // mengambil input dari user

                String[] fieldData = {"Divisi","Nama","Jenis","Barang"};
                String[] tempData = new String[4];

                st = new StringTokenizer(data,",");
                String originalData = st.nextToken();
                
                for (int i = 0; i < fieldData.length; i++) {
                    boolean isUpdate = getYesorNo("apakah anda ingin merubah " + fieldData[i]);
                    originalData = st.nextToken();
                    
                    if (isUpdate) {
                        // user input
                        userInput = new Scanner(System.in);
                        System.out.print("\nMasukan " + fieldData[i] + " baru: ");
                        tempData[i] = userInput.nextLine();
                    } else {
                        tempData[i] = originalData;
                    }
                }
                

                // tampilkan data baru ke layar
                st = new StringTokenizer(data,",");
                st.nextToken();
                System.out.println("\nData baru anda adalah ");
                System.out.println("---------------------------------------");
                System.out.println("Divisi          : " + st.nextToken() + " --> " + tempData[0]);
                System.out.println("Nama            : " + st.nextToken() + " --> " + tempData[1]);
                System.out.println("Jenis           : " + st.nextToken() + " --> " + tempData[2]);
                System.out.println("Barang          : " + st.nextToken() + " --> " + tempData[3]);


                boolean isUpdate = getYesorNo("apakah anda yakin ingin mengupdate data tersebut");

                if (isUpdate){

                    // cek data baru di database
                    boolean isExist = cekBarangDiDatabase(tempData,false);

                    if(isExist){
                        System.err.println("data buku sudah ada di database, proses update dibatalkan, \nsilahkan delete data yang bersangkutan");
                        // copy data
                        bufferedOutput.write(data);

                    } else {

                        // format data baru kedalam database
                        String divisi = tempData[0];
                        String nama = tempData[1];
                        String jenis = tempData[2];
                        String barang = tempData[3];

                        // kita bikin primary key
                        long nomorEntry = ambilEntryPerDivisi(nama, divisi) + 1;

                        String namaTanpaSpasi = nama.replaceAll("\\s+","");
                        String primaryKey = namaTanpaSpasi+"_"+divisi+"_"+nomorEntry;

                        // tulis data ke database
                        bufferedOutput.write(primaryKey + "," + divisi + ","+ nama +"," + jenis + ","+barang);
                    }
                } else {
                    // copy data
                    bufferedOutput.write(data);
                }
            } else {
                // copy data
                bufferedOutput.write(data);
            }
            bufferedOutput.newLine();

            data = bufferedInput.readLine();
        }

        // menulis data ke file
        bufferedOutput.flush();

        // delete original database
        database.delete();
        // rename file tempDB menjadi database
        tempDB.renameTo(database);

}

    private static void deleteData() throws IOException{
        //ambil database original
        File database = new File("CRUD/src/database.txt");
        FileReader fileInput = new FileReader(database);
        BufferedReader bufferedInput = new BufferedReader(fileInput);
        
        //database sementara
        File tempDB = new File("CRUD/src/tempDB.txt");
        FileWriter fileOutput = new FileWriter(tempDB);
        BufferedWriter bufferedOutput = new BufferedWriter(fileOutput);

        //tampilkan data
        System.out.println("List Barang");
        tampilkanData();

        //ambil user input untuk delete data
        Scanner userInput = new Scanner(System.in);
        System.out.print("\nMasukan nomor barang yang akan dihapus: ");
        int deleteNum = userInput.nextInt();

        //looping untuk membaca tiap data entry dan skip data
        boolean isFound = false;
        int entryCounts = 0;

        String data = bufferedInput.readLine();

        while (data != null){
            entryCounts++;
            boolean isDelete = false;

            StringTokenizer st = new StringTokenizer(data,",");

            // tampilkan data yang ingin di hapus
            if (deleteNum == entryCounts){
                System.out.println("\nData yang ingin anda hapus adalah: ");
                System.out.println("-----------------------------------");
                System.out.println("Referensi       : " + st.nextToken());
                System.out.println("Divisi          : " + st.nextToken());
                System.out.println("Nama            : " + st.nextToken());
                System.out.println("Jenis           : " + st.nextToken());
                System.out.println("Barang          : " + st.nextToken());

                isDelete = getYesorNo("Apakah anda yakin akan menghapus?");
                isFound = true;
            }

            if(isDelete){
                //skip pindahkan data dari original ke sementara
                System.out.println("Data berhasil dihapus");
            } else {
                // kita pindahkan data dari original ke sementara
                bufferedOutput.write(data);
                bufferedOutput.newLine();
            }
            data = bufferedInput.readLine();
        }

        if(!isFound){
            System.err.println("Barang tidak ditemukan");
        }

        // menulis data ke file
        bufferedOutput.flush();
        // delete original file
        database.delete();
        // rename file sementara ke database
        tempDB.renameTo(database);

    }

    private static void tambahData() throws IOException{

        FileWriter fileOutput = new FileWriter("CRUD/src/database.txt", true);
        BufferedWriter bufferOutput = new BufferedWriter(fileOutput);

        //mengambil input dari user
        Scanner userInput = new Scanner(System.in);
        String divisi, nama, kategori, barang;

        System.out.print("Masukkan Nama Divisi: ");
        divisi = userInput.nextLine();
        System.out.print("Masukkan Nama Karyawan: ");
        nama = userInput.nextLine();
        System.out.print("Masukkan Kategori: ");
        kategori = userInput.nextLine();
        System.out.print("Masukkan Nama Barang: ");
        barang = userInput.nextLine();


        //cek barang di database

        String[] keywords = {divisi+","+nama+","+kategori+","+barang};
        System.out.println(Arrays.toString(keywords));

        boolean isExist = cekBarangDiDatabase(keywords,false);
        
        //memasukkan data di database
        if (!isExist) {
            // Vyan_Service_1,Service,Vyan,Alat Tulis,Buku
            System.out.println(ambilEntryPerDivisi(divisi, nama));
            long nomorEntry = ambilEntryPerDivisi(divisi, nama) + 1;

            String namaTanpaSpasi = nama.replaceAll("\\s+", "");
            String primaryKey = namaTanpaSpasi+"_"+divisi+"_"+nomorEntry;
            System.out.println("\nData yang akan anda masukkan adalah: ");
            System.out.println("------------------------------------------");
            System.out.println("Primary Key : " + primaryKey);
            System.out.println("Divisi      : " + divisi);
            System.out.println("Nama        : " + nama);
            System.out.println("Kategori    : " + kategori);
            System.out.println("Barang      : " + barang);

            boolean isTambah = getYesorNo("Apakah anda ingin menambah data tersebut?");

            if(isTambah){
                bufferOutput.write(primaryKey + "," + divisi + "," + nama + "," + kategori + "," + barang);
                bufferOutput.newLine();
                bufferOutput.flush();
            }

        } else {
            System.out.println("Barang yang anda akan masukkan sudah tersedia pada database dengan data berikut");
            cekBarangDiDatabase(keywords, true);
        }

        bufferOutput.close();
    }

    private static long ambilEntryPerDivisi(String nama, String divisi) throws IOException {
        FileReader fileInput = new FileReader ("CRUD/src/database.txt");
        BufferedReader bufferInput = new BufferedReader(fileInput);

        long entry = 0;
        String data = bufferInput.readLine();
        Scanner dataScanner;
        String primaryKey;

        while(data !=null){
            dataScanner = new Scanner(data);
            dataScanner.useDelimiter(",");
            primaryKey = dataScanner.next();
            dataScanner = new Scanner(primaryKey);
            dataScanner.useDelimiter("_");

            nama = nama.replaceAll("\\s+", "");
            
            if (divisi.equalsIgnoreCase(dataScanner.next()) && nama.equalsIgnoreCase(dataScanner.next())){
                entry = dataScanner.nextInt();
            }

            data = bufferInput.readLine();
        }
        
        return entry;
    } 

    private static void cariData() throws IOException{

        // membaca database
        
        try {
            File file = new File("CRUD/src/database.txt");
        } catch (Exception e){
            System.err.println("Database Tidak Ditemukan");
            System.err.println("Silahkan tambah data terlebih dahulu");
            tambahData();
            return;
        }

        // ambil keyword

        Scanner userInput = new Scanner(System.in);
        System.out.print("Masukkan kata kunci untuk mencari barang: ");
        String cariString = userInput.nextLine();
        String[] keywords = cariString.split("\\s+");

        // cek keyword di database
        cekBarangDiDatabase(keywords, true);

    }
    
    private static boolean cekBarangDiDatabase(String[] keywords, boolean isDisplay) throws IOException{

    FileReader fileInput = new FileReader("CRUD/src/database.txt");
    BufferedReader bufferInput = new BufferedReader(fileInput);

    String data = bufferInput.readLine();
    boolean isExist = false;
    int nomorData = 0;

    if (isDisplay){
        System.out.println("\n| No |\tDivisi   |\tNama    |\tKategori        |\tBarang");
        System.out.println("=========================================================================");
    }

    while(data !=null){
       
        // cek keywords dalam baris
        isExist = true;
          
        for(String keyword:keywords){
            isExist = isExist && data.toLowerCase().contains(keyword.toLowerCase());
            
        }

        //keywords cocok = tampilkan

        if(isExist){
            if (isDisplay){
            nomorData++;
            StringTokenizer stringToken = new StringTokenizer(data, ",");

            stringToken.nextToken();
            System.out.printf("| %2d ", nomorData);
            System.out.printf("|\t%7s  ",stringToken.nextToken());
            System.out.printf("|\t%-7s ",stringToken.nextToken());
            System.out.printf("|\t%-15s ",stringToken.nextToken());
            System.out.printf("|\t%s ",stringToken.nextToken());
            System.out.println("\n");
            } else {
                break;
        }
    }

            data = bufferInput.readLine();
        }

        if (isDisplay){

        System.out.println("=========================================================================");
        }

        return isExist;
    }

    private static void tampilkanData() throws IOException{

        FileReader fileInput;
        BufferedReader bufferInput;
        
        try {
            fileInput = new FileReader("CRUD/src/database.txt");
            bufferInput = new BufferedReader(fileInput);
        } catch (Exception e){
            System.err.println("Database Tidak Ditemukan");
            System.err.println("Silahkan tambah data terlebih dahulu");
            tambahData();
            return;
        }

        System.out.println("\n| No |\tDivisi |\tNama                |\tKategori               |\tBarang");
        System.out.println("=========================================================================");
        
        String data = bufferInput.readLine();
        int nomorData = 0;
        while(data !=null) {
            nomorData++;

            StringTokenizer stringToken = new StringTokenizer(data, ",");

            stringToken.nextToken();
            System.out.printf("| %2d ", nomorData);
            System.out.printf("|\t%7s  ",stringToken.nextToken());
            System.out.printf("|\t%-7s ",stringToken.nextToken());
            System.out.printf("|\t%-15s ",stringToken.nextToken());
            System.out.printf("|\t%s ",stringToken.nextToken());
            System.out.println("\n");

            data = bufferInput.readLine();
        }
        System.out.println("=========================================================================");
    }

    private static boolean getYesorNo(String message){
        Scanner userInput = new Scanner(System.in);
        System.out.print("\n"+message+" (yes/no)? ");
        String pilihanUser = userInput.next();

        while (!pilihanUser.equalsIgnoreCase("yes") && !pilihanUser.equalsIgnoreCase("no")) {
            System.err.println("Silahkan pilih antara yes atau no");
            System.out.print("\n"+message+" (yes/no)? ");
            pilihanUser = userInput.next();
        }

        return pilihanUser.equalsIgnoreCase("yes");
    }

    private static void clearScreen(){
        try {
            if (System.getProperty("os.name").contains("Windows")){
                new ProcessBuilder("cmd","/c","cls").inheritIO().start().waitFor();
            }
        } catch (Exception ex){
        System.err.println("tidak bisa clear screen");
        }
    }
}
