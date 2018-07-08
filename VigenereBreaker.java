package VigenereProgram;

import java.util.*;
import edu.duke.*;
import java.io.*;
public class VigenereBreaker {
    
    int maincount = 0;
    int[] langKey = new int[100];
    
    String dectyptlang = "";
    public String sliceString(String message, int whichSlice, int totalSlices) {
        //REPLACE WITH YOUR CODE
        StringBuilder sb = new StringBuilder();
        for(int k=whichSlice; k<message.length(); k += totalSlices)
        {
            sb.append(message.charAt(k));
        }
        return sb.toString();
    }

    public int[] tryKeyLength(String encrypted, int klength, char mostCommon) {
        int[] key = new int[klength];
        CaesarCracker cc = new CaesarCracker();
        for(int k=0; k< klength; k++)
        {
            String slice =  sliceString(encrypted, k, klength);
            key[k] = (cc.getKey(slice));
        }
        return key;
    }

    public void breakVigenere (){
        FileResource fr = new FileResource();
        String encrypted = fr.asString();
        HashMap<String, HashSet<String>> map = new HashMap<String, HashSet<String>>();
        DirectoryResource dr = new DirectoryResource();
        for(File f : dr.selectedFiles())
        {
           String fname = f.getName();
           FileResource ff = new FileResource(f);
           HashSet<String> dict = readDictionary(ff);
           map.put(fname, dict);
        }
        /*HashSet<String> dict = readDictionary(fr2);
        breakForLanguage(encrypted,dict);*/
        breakForAllLangs(encrypted, map);
    }
    
    public HashSet<String> readDictionary(FileResource fr)
    {
        HashSet<String> dict = new HashSet<String>();
        for(String line : fr.lines())
        {
            dict.add(line.toLowerCase());
        }
        
        return dict;
    }
    
    public int countWords(String messege, HashSet<String> dict)
    {
        String[] wordsArr = messege.split("\\W+");
        int count = 0;
        for(String word : wordsArr)
        {
            if(dict.contains(word.toLowerCase()))
            {
                count++;
            }
            
        }
        return count;
    }
    
    
    public void  breakForLanguage( String encrypted,HashSet<String> dict, char commonword, String lang )
    {
        int max = 0; int[] arraykey= new int[100]; 
        for(int k=1; k <= 100; k++)
        {
            int[] key = tryKeyLength(encrypted, k, commonword);
            VigenereCipher vc = new VigenereCipher(key);
            String dec = vc.decrypt(encrypted);
            //FileResource fr = new FileResource();
            //dict = readDictionary(fr);
            int count = countWords(dec,dict);
            if(max < count)
            {
                max = count;
                arraykey= key;
            }
            
        }
        
        if(max > maincount)
        {
            maincount = max;
            langKey = arraykey;
            dectyptlang  = lang;
        }
       /*VigenereCipher vc = new VigenereCipher(arraykey);
        String dec = vc.decrypt(encrypted);
        System.out.println("decrypted text is: "+dec + "==> " +  arraykey.length + "with "+max +" word matched" +" and key is follow ");
        for(int k : arraykey)
        {
            System.out.println(k);
        }*/
    }
    
    public char mostCommonCharIn(HashSet<String> dict)
    {
        HashMap< Character, Integer> map = new HashMap< Character, Integer>(); 
        int max = 0; int count = 0; char mostcommonch = 'c';
        for(String s : dict)
        {
            for(int k=0; k<s.length(); k++)
            {
                char ch = s.charAt(k);
                if(map.containsKey(ch))
                {
                     map.put(ch, map.get(ch)+1);
                }
                else
                {
                    map.put(ch,+1);
                }
            }
        }
        
        for(Character ch : map.keySet())
        {
            count = map.get(ch);
            if(max < count)
            {
                max = count;
                mostcommonch = ch;
            }
        }
        return mostcommonch;
    }
    
    public void breakForAllLangs(String encrypted, HashMap<String, HashSet<String>> languages)
    {
        for(String lang : languages.keySet())
        {
            HashSet<String> worddict = languages.get(lang);
            char mostCommon = mostCommonCharIn(worddict);
            breakForLanguage(encrypted, worddict, mostCommon, lang);
        }
        
        VigenereCipher vc = new VigenereCipher(langKey);
        String dec = vc.decrypt(encrypted);
        System.out.println("decrypted text is: "+dec + "==> " +  langKey.length + "with "+maincount +" word matched" +" and key is follow ");
        for(int k : langKey)
        {
            System.out.print(k + " ");
        }
        System.out.println("Language of msg is : "+dectyptlang);
    }
    
}
