import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class testHMM {
    public static void main(String[] args) {
        HMM hmm = new HMM(2,3);
        hmm.pi[0] =1.0;
        hmm.pi[1] =0.0;

        hmm.a[0][0] =0.5;
        hmm.a[0][1]=0.5;
        hmm.a[1][1]=0.5;
        hmm.a[1][0]=0.5;

        hmm.b[0][0] = 1.0/3.0;
        hmm.b[0][1] = 1.0/3.0;
        hmm.b[0][2] = 1.0/3.0;
        hmm.b[1][0] = 1.0/3.0;
        hmm.b[1][2] = 1.0/3.0;
        hmm.b[1][1] = 1.0/3.0;

        int steps = 1000;

        try {
            BufferedReader br = new BufferedReader(new FileReader("src/main/resources/crazysoda.seq"));
            int olen = Integer.parseInt(br.readLine());
            int[] o = new int[olen];
            String os = br.readLine();
            for(int i=0; i< olen; i++){
                o[i] = Integer.parseInt(os.substring(i, i+1));
            }

            System.out.println("Initial Parameters:");
            hmm.print();

            hmm.train(o, steps);

            System.out.println();

            System.out.println("Trained Model:");
            hmm.print();
            int[] oo = {1,2,1,1};
            int[] res = hmm.vertibiDecode(oo);
            System.out.println(Arrays.toString(res));

        } catch (IOException e){
            System.out.println("读取文件时遇到错误");
            System.exit(0);
        }
    }
}
