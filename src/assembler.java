import java.io.File;
import java.text.DecimalFormat;
import java.util.Scanner;
import java.lang.String;

///Developed by Mohamed Ahmed for AAST;

public class assembler {
    public static void main(String[] args) throws Exception{
        File file = new File("inSic.txt");
        int count=0;
        String [] label = new String[19];
        String [] locat = new String[19];
        String [] instruct = new String[19];
        String [] address = new String[19];
        String [] opcode = new String[19];
        String word = null;
        Scanner scanner = new Scanner(file);
        int i=0;
        while (scanner.hasNextLine()){
            word = scanner.nextLine();
            String[] test = word.split("	");
            if(test[0].matches(".*[A-Z].*")){  //if next line start in vaild word
                if(test[0].matches("END")){ //treat END case
                    instruct[i] = test[0];
                    address[i] = test[1];
                    label[i] = "----";
                } else {                        //treat all other cases
                    instruct[i] = test[1];
                    label[i] = test[0];
                    address[i] = test[2];
                    count++;
                }
                i++;
            } else{                                 //if not valid word treat as END
                label[i] = "----";
                instruct[i] = test[1];
                address[i] = test[2];
                i++;
            }
        }
        locat[0] = address[0];             //first two instructions take same address;
        locat[1] = address[0];
        String hexa=null;
        int dec,chara,y=0;
        for(int j=1;j< locat.length-1;j++){         //deal with irrugelars;
            if(instruct[j].matches("RESW")){
                dec=Integer.parseInt(address[j])*3;  //multiply decimal address by 3;
                hexa=Integer.toHexString(dec+Integer.parseInt(locat[j],16)); //convert to hexa(radix =16);
                locat[j+1]=hexa + "";   //give memory to slot and assign next address location;
            }
            else if (instruct[j].matches("RESB")){
                dec=Integer.parseInt(address[j])*1;  //multiply decimal address by 1;
                hexa=Integer.toHexString(dec+Integer.parseInt(locat[j],16)); //convert to hexa(radix =16);
                locat[j+1]=hexa + "";   //give memory to slot and assign next address location;
            }
            else if (instruct[j].matches("BYTE")){
                chara = address[j].length()-3; //remove 3 chars (X' ');
                if(address[j].startsWith("X")){
                    dec = chara/2;
                    hexa=Integer.toHexString(dec+Integer.parseInt(locat[j],16));
                    locat[j+1] = hexa + "";
                } else{   // if Byte is (C' ')
                    hexa= Integer.toHexString(chara+Integer.parseInt(locat[j],16));
                    locat[j+1] = hexa+"";
                }
            }
            else if(instruct[j].matches("WORD")){       //Indexed Irregulars
                if(address[j].contains(",")){
                    String [] m3dawy=address[j].split(",");
                    dec=Integer.parseInt(locat[j],16)+(3*m3dawy.length);
                    locat[j+1]=Integer.toHexString(dec);
                }
                else {
                    dec = Integer.parseInt(locat[j], 16) + 3;
                    locat[j + 1] = Integer.toHexString(dec);
                }
            }
            else{
                dec=Integer.parseInt(locat[j],16)+3;
                locat[j+1]=Integer.toHexString(dec);
            }
        }
        String[] symbol1=new String [count];   // for symboltable
        String[] symbol2=new String [count]; // for locations
        for(int n=0;n< instruct.length;n++){
            if(!label[n].matches("----") && !label[n].matches("END")){
                symbol1[y]=label[n];
                symbol2[y]=locat[n];
                y++;
            }
        }
        String awal=null, a5er=null, tester=null, tester2="0";    //for opcodes table
        int far2=0;
        for(int w=0;w< opcode.length;w++){
            if(instruct[w].matches("RESW") || instruct[w].matches("RESB") || instruct[w].matches("Start") || instruct[w].matches("END"))
            {
                opcode[w]="----";
            }
            else if (instruct[w].matches("WORD")){
                DecimalFormat df = new DecimalFormat("000000");
                if(address[w].contains(",")){
                    opcode[w]="";
                    String [] m3dawy2=address[w].split(",");
                    for(int s=0;s< m3dawy2.length;s++){
                        far2=Integer.parseInt(m3dawy2[s]);
                        tester=Integer.toHexString(far2);
                        opcode[w]=opcode[w]+tester+" ";
                    }
                }
                else{
                    far2=Integer.parseInt(address[w]);
                    tester=Integer.toHexString(far2);
                    String xxxx= df.format(Integer.parseInt(tester));   ///0009 masalan
                    opcode[w]= xxxx;
                }
            }
            else if (instruct[w].matches("BYTE")){
                if (address[w].contains("X"))   //X' ' Mslan
                {
                    tester=address[w].substring(2, address[w].length()-1);  //men awl talet char
                    opcode[w]=tester;
                }
                else{
                    char [] xxxx=address[w].substring(2, address[w].length()-1).toCharArray();
                    for(int q=0;q< xxxx.length;q++){
                        tester=tester+(int)xxxx[q];
                    }
                    opcode[w]=tester;
                }
            }
            else {
                awal=ta7wel.geeb(instruct[w]);
                String [] tempo=address[w].split(",");
                tester=address[w];
                if(tempo.length == 2){
                    tester=tempo[0];
                }
                for(int p=0;p<symbol1.length;p++){
                    if (tester.matches(symbol1[p])){
                        a5er=symbol2[p];       ///makan el symbol table
                    }
                    if(tempo.length == 2){
                        far2=Integer.valueOf(a5er)+1400;
                        a5er=far2+"";
                    }
                }
                opcode[w]=awal.concat(a5er);
            }
        }
        System.out.println("LOCATION"+"\t \t"+"LABEL"+"\t \t \t"+"INSTRUCT"+"\t \t"+"ADDRESS"+"\t \t"+"CODE");
        for(int f=0;f< instruct.length;f++){  /// print program full table
            System.out.println(" "+locat[f]+"\t \t \t"+label[f]+"\t \t \t"+instruct[f]+"\t \t \t"+address[f]+"\t \t \t"+opcode[f]);
        }
        System.out.println("\n"+"SYMBOL"+"\t \t"+"LOCATION");    //print symbol table
        for (int r=0;r<symbol1.length;r++){
            System.out.println(" "+symbol1[r]+"\t \t \t"+symbol2[r]);
        }
        int totallen=Integer.parseInt(locat[18],16)-Integer.parseInt(locat[0],16);
        String lengthhex=Integer.toHexString(totallen);
        System.out.println("\n"+"HTE RECORD");
        System.out.println("H $ "+label[0]+" $ "+locat[0]+" $ "+lengthhex);
        totallen=Integer.parseInt(locat[12],16)-Integer.parseInt(locat[0],16);
        lengthhex=Integer.toHexString(totallen);
        System.out.println("T $ "+lengthhex+" $ ");
        int u=1;
        while (!instruct[u].matches("RESW")){   ///awl 11 instructions
            System.out.println(opcode[u]+" ");
            u++;
        }
        totallen=Integer.parseInt(locat[locat.length-1],16)-Integer.parseInt(locat[u+2],16);
        lengthhex=Integer.toHexString(totallen);
        System.out.println("\n"+"T $ "+lengthhex+" $ ");
        for (u = u+2;u< locat.length;u++){
            System.out.println(opcode[u]+" ");
        }
        System.out.println("\n"+"E $ "+locat[0]);     ///end record
    }
}
