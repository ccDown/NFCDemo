package com.soullistener.nfcdemo;

import java.io.UnsupportedEncodingException;
/**
 *
 * @description 字符串处理工具类
 */
public class StringUtil {

    /**
     * 判断字符串是否为空
     *
     * @param value
     * @return
     */
    public static boolean isEmpty(String value) {
        if (value != null && !"".equalsIgnoreCase(value.trim())
                && !"null".equalsIgnoreCase(value.trim())) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 十六进制的ASCII码转字符串
     * @param hex
     * @return
     */
    public static String convertHexToString(String hex){
        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();
        for( int i=0; i<hex.length()-1; i+=2 ){
            String output = hex.substring(i, (i + 2));
            int decimal = Integer.parseInt(output, 16);
            sb.append((char)decimal);
            temp.append(decimal);
        }
        return sb.toString();
    }

    /**
     * 字节数组转十六进制字符串
     * */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 16进制转ascii
     * @param string
     * @return
     */
    public static String converStringtoAscll(String string){
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0;i<string.length();i++){
            stringBuilder.append((int)string.charAt(i));
        }
        return stringBuilder.toString();
    }

    /**
     *
     * @param hexString
     * @return 将十六进制转换为字节数组
     */
    public static String hexStr =  "0123456789ABCDEF";
    public static byte[] hexStringToBinary(String hexString){
        int len = hexString.length()/2;
        byte[] bytes = new byte[len];
        byte high = 0;
        byte low = 0;

        for(int i=0;i<len;i++){
            high = (byte)((hexStr.indexOf(hexString.charAt(2*i)))<<4);
            low = (byte)hexStr.indexOf(hexString.charAt(2*i+1));
            bytes[i] = (byte) (high|low);
        }
        return bytes;
    }

    /**左补指定元素*/
    public static String fillLeft(String value, String element, int count){
        StringBuffer sb = new StringBuffer();
        for(int i=0;i<count - value.length();i++){
            sb.append(element);
        }
        sb.append(value);
        return sb.toString();
    }

    /**右补指定元素*/
    public static String fillRight(String value, String element, int count){
        StringBuffer sb = new StringBuffer();
        sb.append(value);
        for(int i=0;i<count - value.length();i++){
            sb.append(element);
        }
        return sb.toString();
    }

    //10进制转16进制
    public static String IntToHex(int n){
        char[] ch = new char[20];
        int nIndex = 0;
        while ( true ){
            int m = n/16;
            int k = n%16;
            if ( k == 15 )
                ch[nIndex] = 'F';
            else if ( k == 14 )
                ch[nIndex] = 'E';
            else if ( k == 13 )
                ch[nIndex] = 'D';
            else if ( k == 12 )
                ch[nIndex] = 'C';
            else if ( k == 11 )
                ch[nIndex] = 'B';
            else if ( k == 10 )
                ch[nIndex] = 'A';
            else
                ch[nIndex] = (char)('0' + k);
            nIndex++;
            if ( m == 0 )
                break;
            n = m;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(ch, 0, nIndex);
        sb.reverse();
//        String strHex = new String("0x");
//        strHex += sb.toString();
        return sb.toString();
    }

    // 16进制转10进制
    public static int HexToInt(String strHex) {
        int nResult = 0;
        if (!IsHex(strHex))
            return nResult;
        String str = strHex.toUpperCase();
        if (str.length() > 2) {
            if (str.charAt(0) == '0' && str.charAt(1) == 'X') {
                str = str.substring(2);
            }
        }
        int nLen = str.length();
        for (int i = 0; i < nLen; ++i) {
            char ch = str.charAt(nLen - i - 1);
            try {
                nResult += (GetHex(ch) * GetPower(16, i));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return nResult;
    }

    // 计算16进制对应的数值
    public static int GetHex(char ch) throws Exception {
        if (ch >= '0' && ch <= '9')
            return (int) (ch - '0');
        if (ch >= 'a' && ch <= 'f')
            return (int) (ch - 'a' + 10);
        if (ch >= 'A' && ch <= 'F')
            return (int) (ch - 'A' + 10);
        throw new Exception("error param");
    }

    // 计算幂
    public static int GetPower(int nValue, int nCount) throws Exception {
        if (nCount < 0)
            throw new Exception("nCount can't small than 1!");
        if (nCount == 0)
            return 1;
        int nSum = 1;
        for (int i = 0; i < nCount; ++i) {
            nSum = nSum * nValue;
        }
        return nSum;
    }

    // 判断是否是16进制数
    public static boolean IsHex(String strHex) {
        int i = 0;
        if (strHex.length() > 2) {
            if (strHex.charAt(0) == '0'
                    && (strHex.charAt(1) == 'X' || strHex.charAt(1) == 'x')) {
                i = 2;
            }
        }
        for (; i < strHex.length(); ++i) {
            char ch = strHex.charAt(i);
            if ((ch >= '0' && ch <= '9') || (ch >= 'A' && ch <= 'F')
                    || (ch >= 'a' && ch <= 'f'))
                continue;
            return false;
        }
        return true;
    }

    /**
     * 转字符串成十六进制的ASCII
     * @param str
     * @return
     */
    public static String convertStringToHex(String str){
        char[] chars = str.toCharArray();
        StringBuffer hex = new StringBuffer();
        for(int i = 0; i < chars.length; i++){
            hex.append(Integer.toHexString((int)chars[i]));
        }
        return hex.toString();
    }

    /**
     * 十六进制字符串转换成bytes
     *
     * @param hexStr
     * @return
     */
    public static byte[] hexStr2Bytes(String hexStr) {
        int l = hexStr.length();
        if (l % 2 != 0) {
            StringBuilder sb = new StringBuilder(hexStr);
            sb.insert(hexStr.length() - 1, '0');
            hexStr = sb.toString();
        }
        byte[] b = new byte[hexStr.length() / 2];
        int j = 0;
        for (int i = 0; i < b.length; i++) {
            char c0 = hexStr.charAt(j++);
            char c1 = hexStr.charAt(j++);
            b[i] = (byte) ((parse(c0) << 4) | parse(c1));
        }
        return b;
    }

    private static int parse(char c) {
        if (c >= 'a')
            return (c - 'a' + 10) & 0x0f;
        if (c >= 'A')
            return (c - 'A' + 10) & 0x0f;
        return (c - '0') & 0x0f;
    }

    /**解析44域汉字错误信息描述*/
    public static String parseErrorMsg(String msg){
        String parseStr = null;
        try {
            parseStr = new String(hexStringToByte(msg.toUpperCase()),"GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return parseStr;
    }

    public static byte[] hexStringToByte(String hex) {
        int len = hex.length() / 2;
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();

        for(int i = 0; i < len; ++i) {
            int pos = i * 2;
            result[i] = (byte)(toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }

        return result;
    }

    private static byte toByte(char c) {
        byte b = (byte)"0123456789ABCDEF".indexOf(c);
        return b;
    }

    /**格式姓名*/
    public static String formateName(String name){
        String nameStr = "";
        int length = name.length();
        if(length == 2){
            nameStr = name.substring(0,1);
            nameStr += "*";
        }else if(length == 3){
            nameStr = name.substring(0,1);
            nameStr += "**";
        }

        return nameStr;
    }

    /**
     * 16进制数字取反
     *
     * @param hexString
     * @return
     */
    public static String hexBalanceNo(String hexString) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < hexString.length(); i++) {
            stringBuilder.append(Integer.toHexString(~Integer.valueOf(hexString, 16)).substring(7, 8));
        }
        return stringBuilder.toString();
    }

    /**
     * 16进制数字取反
     *
     * @param hexString
     * @return
     */
    public static String hexNo(String hexString) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < hexString.length(); i++) {
            String noHex = Integer.toHexString(~Integer.valueOf(String.valueOf(hexString.charAt(i)), 16));
            stringBuilder.append(noHex.substring(7, 8));
        }
        return stringBuilder.toString();
    }

    public static byte[] intToBytes(int value) {
        byte[] src = new byte[4];
        src[3] = (byte) ((value >> 24) & 0xFF);
        src[2] = (byte) ((value >> 16) & 0xFF);
        src[1] = (byte) ((value >> 8) & 0xFF);
        src[0] = (byte) (value & 0xFF);
        return src;
    }

    /**
     * 十六进制转int 大端模式
     *
     * @param hex
     * @return
     */
    public static int hex2Int(String hex) {
        byte[] bytes = hex2Bytes(hex);
        int len = bytes.length;
        int rec = 0;
        for (int i = 0; i < len; i++) {
            int temp = bytes[i] & 0xff;
            int off = (len - 1 - i) * 8;
            rec |= (temp << off);
        }
        return rec;
    }

    /**
     * 十六进制转Byte
     *
     * @param hexStr
     * @return
     */
    public static byte[] hex2Bytes(String hexStr) {
        int len = hexStr.length();
        if (len % 2 != 0) {
            throw new RuntimeException("length error");
        }
        byte[] b = new byte[hexStr.length() / 2];
        int bLen = b.length;
        int j = 0;
        for (int i = 0; i < bLen; i++) {
            char c0 = hexStr.charAt(j++);
            char c1 = hexStr.charAt(j++);
            b[i] = (byte) ((parse(c0) << 4) | parse(c1));
        }
        return b;
    }
}
