import java.io.*;
import java.util.*; 


public class WordCountJ{
    public static void main(String args[]){
        int nfiles; 
        nfiles = args.length;
        if ( nfiles == 0 ) {
            System.out.println("Input Dosya bulunamadı ...");
            System.exit(0); 
        }

        System.out.println("Input dosyalar:");
        for ( int i = 0 ; i < args.length ; i++ ) {
            System.out.println(args[i]);
        }

        List< Map<String, Integer> > wordCounts 
            = new ArrayList< Map<String, Integer> >();

        // Thread listesi oluşturuluyor
        ArrayList<ThreadCountOneFile> threadlist 
            = new ArrayList<ThreadCountOneFile>();
        for ( int i = 0 ; i < args.length ; i++ ) {
            ThreadCountOneFile t = new ThreadCountOneFile(args[i]);
            threadlist.add( t );
            t.start();
        }

        for ( ThreadCountOneFile t: threadlist ) {
            try{
                t.join();
                wordCounts.add( t.getWordCount() );
            } catch (Exception ex){}
        }

        Map<String, Integer> totalWCount = new HashMap<String, Integer>();
        for ( Map<String, Integer> wcount: wordCounts ) {
            for (Map.Entry<String, Integer> entry : wcount.entrySet())
            {
                String iword = entry.getKey();
                Integer icount = entry.getValue();
                Integer count = totalWCount.get(iword);
                totalWCount.put(iword, count == null ? icount : count + icount);
            }           
        }
        
        System.out.println("-- Toplam kelime sayısı listeleniyor --");
        for ( Map.Entry<String, Integer> entry : totalWCount.entrySet() )
        {
            System.out.println(entry.getKey() + "      " + entry.getValue());
        }
    }
}

class ThreadCountOneFile extends Thread
{
    private String _filename;
    private Map<String, Integer> _wordCount;

    public ThreadCountOneFile(String fname)
    {
        this._filename = fname;
        this._wordCount = new HashMap<String, Integer>();
    }
    
    public Map<String, Integer> getWordCount()
    {
        return this._wordCount;
    }

    @Override
    public void run()
    {
         try{
            BufferedReader br = new BufferedReader(new FileReader(_filename));
            String line;
            while ((line = br.readLine()) != null) {
                for (String word: line.split("\\s+")){
                    // trim 
                    word = word.replaceFirst("[^a-zA-Z0-9\\s]*", "");
                    word = new StringBuffer(word).reverse().toString();
                    word = word.replaceFirst("[^a-zA-Z0-9\\s]*", "");
                    word = new StringBuffer(word).reverse().toString();

                    Integer count = _wordCount.get(word);
                    _wordCount.put(word, count == null ? 1 : count + 1);
                }
            }
            br.close();


            } catch(IOException e) {
                System.out.print("Exception");
            }

    }
}
