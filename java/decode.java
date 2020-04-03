import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author tr1ple
 */


public class decode {
    public HashMap<String, String> codingTable;
    public String tableStr;

    /**
     * todo : 还原编码表
     */
    public decode() throws IOException {
        FileInputStream fi = new FileInputStream(new File(System.getProperty("user.dir")+"/javasec-basic/src/main/resources/table.txt"));
        int size = fi.available();
        byte[] str = new byte[size];
        fi.read(str);
        this.tableStr = new String(str);
        //System.out.println(this.tableStr);
        codingTable  = new HashMap<String, String>();
    }

    /**
     * todo : 生成编码表
     */
    public void generateCodingTable(){
       String[] tableStr = this.tableStr.split("\\|");
       for(int i=0;i<tableStr.length;i++){
           String[] tmp = tableStr[i].split("=");
           String left = tmp[1];
           String right = tmp[0];
           this.codingTable.put(left,right);
          // System.out.println(String.valueOf(i)+":"+tableStr[i]);
       }
       System.out.println("[*] 编码表为:");
        System.out.println(this.codingTable);
    }

    /**
     * todo : 读取编码结果
     * @return
     * @throws IOException
     */
    public  String readStr() throws IOException {
        FileInputStream fi = new FileInputStream(new File(System.getProperty("user.dir")+"/javasec-basic/src/main/resources/result.txt"));
        int size = fi.available();
        byte[] str = new byte[size];
        fi.read(str);
        return new String(str);
    }

    /**
     * todo : 输出解码结果
     * @param str
     * @return
     */
    public String  outputStr(String str){
        String result = "";
        String tmp = "";
        int length = str.length();
        for(int i=0;i < length;i++){
            tmp = tmp + str.charAt(i);
            if(this.codingTable.containsKey(tmp)){
                result = result + this.codingTable.get(tmp);
                tmp = "";
            }
        }
        return result;
    }

    public static void main(String[] args) throws IOException {
        decode obj = new decode();
        String str = obj.readStr();
        System.out.println("[*] 需要编码的字符串为:");
        System.out.println(str);
        obj.generateCodingTable();
        String result = obj.outputStr(str);
        System.out.println("[*] 解码后的字符串为:");
        System.out.println(result);
    }

}
