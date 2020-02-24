import java.util.Arrays;

public class testVertibi {
    public static void main(String[] args) {
        HMM hmm = new HMM(3, 2);
        hmm.pi[0] = 0.2;
        hmm.pi[1] = 0.4;
        hmm.pi[2] = 0.4;

        hmm.a[0][0] =0.5 ;hmm.a[0][1] =0.2 ;hmm.a[0][2] = 0.3;
        hmm.a[1][0] = 0.3 ;hmm.a[1][1] =0.5 ;hmm.a[1][2] = 0.2;
        hmm.a[2][0] = 0.2 ;hmm.a[2][1] =0.3 ;hmm.a[2][2] =0.5;

        hmm.b[0][0]= 0.5; hmm.b[0][1]=0.5;
        hmm.b[1][0]=0.4 ;hmm.b[1][1]= 0.6;
        hmm.b[2][0]=0.7 ;hmm.b[2][1]= 0.3;

        int[] o = {0,1, 0};
        int[] res = hmm.vertibiDecode(o);
        System.out.println(Arrays.toString(res));
    }


}
