package edu.wisc.cs.will.Utils;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.io.File;


class DiscPred{
	String prename=null;
	public String[] argsloc=null;
	public String[] binNum = null;
}
public class disc {	
	
	public static DiscPred trial(String line)
	{
	DiscPred D = new DiscPred();
	String[] mode = line.split("\\:");
	String[] pred = null;
	String[] temp = null;
	String[] args = null;
	String[] bins = null;
	if (mode[0].equals("disc"))
	{
		pred = mode[1].split("\\(\\[");
		pred[0]=pred[0].replace(" ", "");
		temp = pred[1].split("\\,\\[");
		temp[0] = temp[0].replace("]", "");
		temp[1] = temp[1].replace("]).", "");
		args=temp[0].split(",");
		bins=temp[1].split(",");
		}
	D.argsloc=args;
	D.binNum=bins;
	D.prename=pred[0];
	return D;	
	}
    public void Discretization(String DirectoryPath) throws IOException {
    	String [] trial=null;
    	trial=DirectoryPath.split("\\\\");
    	String bkdp= DirectoryPath;
    	String factsdp=DirectoryPath;
    	String mergefile=DirectoryPath;
    	String delfile=DirectoryPath;
    	String preddp=DirectoryPath;
    	trial[trial.length-1]=trial[trial.length-1].replace("/","");
    	String prefix = trial[trial.length-1];
    	String bkname=prefix+"_bk.txt";
    	String alterbkpath=factsdp;
    	String factspath=factsdp.replace("/","\\"+prefix+"_facts.txt");
    	String predpath=preddp.replace("/","\\"+prefix+"_facts_");
    	String outpath=predpath+"new.txt";
    	FileInputStream fstreamtemp = new FileInputStream(DirectoryPath+bkname);
        BufferedReader brtemp = new BufferedReader(new InputStreamReader(fstreamtemp));
        String strLinetemp;
        String[] bkline=null;
        String bkpath=null;
        boolean check=false;
        while ((strLinetemp = brtemp.readLine()) != null && check==false)  
        {
        	if((strLinetemp.contains("import:"))&&(!strLinetemp.contains("//")))
        	{	bkline=strLinetemp.split("\\/");
        		bkline[1]=bkline[1].replaceAll("\".", "");   
        		bkpath=bkdp.replace(trial[trial.length-1]+"/", bkline[1]);
        		check=true;
        	}
        	else if((!strLinetemp.contains("import:")))
        	{
        		bkpath=alterbkpath.replace("/","\\"+prefix+"_bk.txt");
        		check=true;
        	}
        }
        brtemp.close();

        FileInputStream fstream = new FileInputStream(bkpath);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
        String strLine;
        ArrayList<DiscPred> DP = new ArrayList<DiscPred>();
        while ((strLine = br.readLine()) != null)   {
        	if(strLine.contains("disc"))
        		if(!strLine.contains("//"))
        			DP.add(trial(strLine));
        }
        br.close();

        String strLine1;
        int[] range = new int[50];
        ArrayList<ArrayList<Integer>> multD = new ArrayList<ArrayList<Integer>> ();
        ArrayList<ArrayList<Integer>> threshold = new ArrayList<ArrayList<Integer>> ();
        ArrayList<String> filenames = new ArrayList<>();
        ArrayList<String> prednames = new ArrayList<>();
        for (DiscPred g: DP) 
        {	
        for (String a: g.argsloc)
        {
        ArrayList<Integer> listofNum = new ArrayList<Integer>();

        Integer al = Integer.valueOf(a)-1;
        FileInputStream ostream = new FileInputStream(factspath);
        BufferedReader br1 = new BufferedReader(new InputStreamReader(ostream));
        while ((strLine1 = br1.readLine()) != null)   
        {
        	if(strLine1.contains(g.prename) && !strLine1.contains("//") )
        	{	String[] temp=strLine1.split("\\(");
        	    temp[1]=temp[1].replace(").", "");
        		String[] arg=temp[1].split(",");
        		Integer x = Integer.valueOf(arg[al]);
        		listofNum.add(x);
        	}       	
        }
        Collections.sort(listofNum);
        range[al]=listofNum.size();
    	multD.add(listofNum);	
        ArrayList<Integer> tval = new ArrayList<Integer>();
        int n=0;
        Integer bn = Integer.valueOf(g.binNum[al]);
        int tbn=bn-1;
        while(n<range[al]-1 && tbn>0)
        {	tbn=tbn-1;
        	n=n+((range[al]/bn)-1);
        	tval.add((multD.get(al)).get(n));   
        	n++;
        }
        threshold.add(tval);
        br1.close();
        }                               
        FileInputStream nstream = new FileInputStream(factspath);
        BufferedReader br2 = new BufferedReader(new InputStreamReader(nstream));
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(predpath+g.prename+".txt")));
        filenames.add(prefix+"_facts_"+g.prename+".txt");
        prednames.add(g.prename);
        String strLine2;
        String out= "";
    	while ((strLine2 = br2.readLine()) != null)   
    	{        
    	if(strLine2.contains(g.prename) && !strLine2.contains("//") )
    	{	String[] temp=strLine2.split("\\(");
    	    temp[1]=temp[1].replace(").", "");
    		String[] arg=temp[1].split(",");
            String o="";
    		for(String b:g.argsloc)
    		{
    		 Integer al = Integer.valueOf(b)-1;
    		 Integer a = Integer.valueOf(arg[al]); 
    		 boolean discrete=false;
    		 int i;
    		 for(i=0; i<(threshold.get(al)).size();i++)
    		 {  
    			 if (a>(threshold.get(al)).get(i)){
    				 continue;
    				 }
    			 else 
    			 {
    				 arg[al]=String.valueOf(i);
    				 discrete=true;
    				 break;
    				 }
    		 }
			 if (discrete==false)
			 {
				 arg[al]=String.valueOf(i);
			 }  			
    		}
    		int count = 0;
    		for(String s: arg) 
    		{    
			   o += (count == arg.length-1) ? s: s + ",";
			   count++;
    		}
    		out=g.prename+"("+o+")."+"\n";
    		bw.write(out);
    	}
    	
    	}
    	br2.close();
    	bw.flush();
    	bw.close();
    	}
        BufferedWriter bw1 = new BufferedWriter(new FileWriter(new File(outpath)));
        String strLine3;
        for (String file : filenames) 
        {
            FileInputStream instream = new FileInputStream(mergefile.replace("/", "\\")+file);
            BufferedReader br3 = new BufferedReader(new InputStreamReader(instream));
        	while ((strLine3 = br3.readLine()) != null)   
        	{  
        		bw1.write(strLine3+"\n");
        	}
        	br3.close();
        	        
        }
        for (String file : filenames) 
        {
            File tfile = new File(delfile.replace("/", "\\")+file);
            tfile.delete();
        }
        String strLine4;
        FileInputStream instream2 = new FileInputStream(factspath);
        BufferedReader br4 = new BufferedReader(new InputStreamReader(instream2));
        while ((strLine4 = br4.readLine()) != null && !(strLine4.contains("//")))   
    	{  
        	String[] temp=strLine4.split("\\(");
        	if (!prednames.contains(temp[0]))
        	{
        		bw1.write(strLine4+"\n");
        	}
        	
    	}
        br4.close();
        bw1.close();
        
    } }
