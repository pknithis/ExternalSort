/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package externalsort_multiplefiles;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;




/**
 *
 * @author nithishkp
 * 
 */

class FileData
{
    String data;
    BufferedReader breader;
    File thisFile;
    FileData(BufferedReader breader,File f)
    {        
        this.breader=breader;       
        thisFile=f;
    }
    public boolean readFileData()
    {
        try {
            data = breader.readLine();
        } catch (IOException ex) {
            Logger.getLogger(FileData.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(data == null) 
        {            
            return false;
        }
        else
            return true;
    }
    public String getFileData()
    {
        return data;
    }
    public void closeStream()
    {
        try {
            breader.close();
            thisFile.delete();
        } catch (IOException ex) {
            Logger.getLogger(FileData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
}
class FileDataComparator implements Comparator<FileData>
{
    public int compare(FileData ob1,FileData ob2)
    {
        return ob1.data.compareTo(ob2.data);
    }
}
class MyTmpFileFilter implements FilenameFilter
{
    public boolean accept(File directory, String fileName)
    {
        if(fileName.startsWith("temp_"))
        {
            return true;
        }
        else
            return false;
    }
}

public class ExternalSort_MultipleFiles {
    
    public static void finalSort(File []inputFile,File outputFile)
    {
       File tdir=new File(inputFile[0].getParent());
       File tmpFiles[] = tdir.listFiles(new MyTmpFileFilter()); /* Get all temp files */  
       ArrayList <FileData>tmpFileElements=new ArrayList<FileData>();        
       
       /*Read the temp files and construct a priority queue from the data of the temp files.
         After reading from priority queue, add the data again to priority queue from the temp file 
         from which the data was removed*/
       for(int i=0;i<tmpFiles.length;i++)
       {
           try {               
                 tmpFileElements.add(new FileData(new BufferedReader(new FileReader(tmpFiles[i])),tmpFiles[i]));               
           } catch (FileNotFoundException ex) {
               Logger.getLogger(ExternalSort_MultipleFiles.class.getName()).log(Level.SEVERE, null, ex);
           }
       }       
       PriorityQueue <FileData>fdataQueue=new PriorityQueue<FileData>(tmpFileElements.size(),new FileDataComparator());
       //Populate the priority Queue
       for(int i=0;i<tmpFileElements.size();i++)
       {              
           if(!(tmpFileElements.get(i).readFileData()))
           {
               tmpFileElements.get(i).closeStream(); /* If the data in the temp file is over then close the stream and delete the temp file */
               tmpFileElements.remove(i);               
               i=0;
               continue;
           }
           else
           {
               fdataQueue.add(tmpFileElements.get(i));
           }
           
       }
       
       PrintWriter fout=null;
        try {
            fout=new PrintWriter(new BufferedWriter(new FileWriter(outputFile)));
        } catch (IOException ex) {
            Logger.getLogger(ExternalSort_MultipleFiles.class.getName()).log(Level.SEVERE, null, ex);
        }
       
       while(!fdataQueue.isEmpty())
       {
           FileData fdata = fdataQueue.remove();
           fout.println(fdata.getFileData());/*Write the sorted data from the priiority queue to the final output file */
           
           if(fdata.readFileData())
           {
              fdataQueue.add(fdata);
           }
           else
           {
               fdata.closeStream();
           }
       }       
       fout.close();       
    }

    public static void externalSort(File []inputFile,File outputFile,int memorySize)
    {
        int THRESHOLD_MEMSIZE = memorySize*1024;
        
        /*Read the input files and sort bytes of THRESHOLD_MEMSIZE and write them to temporary file */
        for(int i=0; i<inputFile.length; i++)
        {            
            BufferedReader br = null;            
            String fdata=null;
            try {
                br=new BufferedReader(new FileReader(inputFile[i]));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ExternalSort_MultipleFiles.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
            int tempFileCount = 0;
            int numOfTempFiles = (int) Math.ceil((float)inputFile[i].length()/THRESHOLD_MEMSIZE);
            
            while(tempFileCount<numOfTempFiles)
            {
                PrintWriter pout=null;
                ArrayList <String>readFileData=new ArrayList<String>();
                int currentReadBytes = 0;    
                
                try {
                    pout = new PrintWriter(new FileWriter(inputFile[i].getParent()+"\\"+"temp_"+(tempFileCount++)+inputFile[i].getName()));
                } catch (IOException ex) {
                    Logger.getLogger(ExternalSort_MultipleFiles.class.getName()).log(Level.SEVERE, null, ex);
                }

                try {
                        while((fdata=br.readLine())!=null && currentReadBytes<THRESHOLD_MEMSIZE)
                        {
                            currentReadBytes+=fdata.getBytes().length;
                            readFileData.add(fdata);
                        }

                        Collections.sort(readFileData);

                        for(String sortedString:readFileData)
                        {
                            pout.println(sortedString);
                        }

                        pout.close();
                    
                } catch (IOException ex) {
                    Logger.getLogger(ExternalSort_MultipleFiles.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(ExternalSort_MultipleFiles.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        finalSort(inputFile,outputFile);
        
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the maximum amount of main memory of the system(in KB) : ");
        int memorySize = Integer.parseInt(sc.nextLine());
        if(memorySize == 0)
        {
            System.out.println("Invalid input");
            return;
        }
        System.out.println("Enter the number of Input Files : ");
        int fileCount = Integer.parseInt(sc.nextLine());
        if(fileCount == 0)
        {
            System.out.println("Invalid input");
            return;
        }
        File fileList[] = new File[fileCount];
        System.out.println("Enter the input file names");
        
        for(int i =0;i<fileCount;i++)
        {
            fileList[i]=new File(sc.nextLine());
            if(!fileList[i].exists())
            {
                System.out.println("File doesnot present, re enter file name");
                i--;
                continue;
            }   
        }
        System.out.println("Enter the output file name:");
        File outputFile = new File(sc.nextLine());
        if(!outputFile.exists())
            try {
                outputFile.createNewFile(); /* Create the output file if it doesnt exists */
        } catch (IOException ex) {
            Logger.getLogger(ExternalSort_MultipleFiles.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        externalSort(fileList, outputFile,memorySize);
        System.out.println("Sorting Complete. Please check "+outputFile.getName());
    }
    
}
