import java.text.DecimalFormat;


public class HMM {
    public int numStates; //可能的状态数
    public int sigmaSize; //可能的观测数

    public double pi[]; //初始状态概率
    public double a[][]; //状态转移矩阵
    public double b [][]; //状态发射矩阵

    public HMM(int numStates, int sigmaSize){
        this.numStates = numStates;
        this.sigmaSize = sigmaSize;

        pi = new double[numStates];
        a = new double[numStates][numStates];
        b = new double[numStates][sigmaSize];
    }

    //前向计算
    public double[][] forwardProc(int[] o){
        int T = o.length;
        double[][] alpha = new double[numStates][T];

        for(int i=0; i<numStates;i++)
            alpha[i][0] = pi[i] * b[i][o[0]];

        for (int t=0; t<T-1; t++){
            for(int j=0; j<numStates;j++){
                alpha[j][t+1] = 0;
                for(int i=0;i<numStates;i++)
                    alpha[j][t+1] += (alpha[i][t] *a[i][j]);
                alpha[j][t+1] *= b[j][o[t+1]];
            }
        }
        return alpha;
    }

    //后向计算　
    public double[][] backwardProc(int[] o){
        int T = o.length;
        double[][] beta = new double[numStates][T];
        for (int i = 0;i<numStates;i++)
            beta[i][T-1] = 1;

        for (int t=T-2;t>=0;t--){
            for (int i=0;i<numStates;i++){
                beta[i][t] = 0;
                for(int j=0; j<numStates;j++){
                    beta[i][t] += (beta[j][t+1] * a[i][j] *b[j][o[t+1]]);
                }
            }
        }
        return beta;
    }

    // gamma为P(i_t = q_i|O, lambda)
    public double gamma(int i, int t, int[] o, double[][] alpha, double[][] beta ){
        double num = alpha[i][t] * beta[i][t];
        double denom = 0;
        for(int j=0; j<numStates;j++)
            denom += alpha[j][t] * beta[j][t];
        return divide(num, denom);

    }

    // p为概率P(i_t = q_t, i_(t+１) = q_j|O, lambda)即给定模型lambda和观测O，在时刻t处于状态q_i
    // 在时刻t+1时处于q_j的概率．
    public double p(int t, int i, int j, int[] o, double[][] alpha, double[][] beta){
        double num;
        if (t == o.length -1)
            num = alpha[i][t] * a[i][j];
        else
            num = alpha[i][t] * a[i][j] * b[j][o[t+1]] *beta[j][t+1];

        double denom = 0;
        for (int k=0;k<numStates;k++)
            denom += (alpha[k][t] * beta[k][t]);
        return divide(num, denom);
    }

    //训练过程，利用Baum-Welch算法进行参数估计．
    //P(O|lambda) = \sum_i P(O|I, lambda)P(I|lambda)
    public void train(int[] o, int steps) {
        int T = o.length;
        double[][] alpha;
        double[][] beta;

        double pi1[] = new double[numStates];
        double a1[][] = new double[numStates][numStates];
        double b1[][] = new double[numStates][sigmaSize];

        for(int s=0; s<steps; s++){
            alpha = forwardProc(o);
            beta = backwardProc(o);

            for(int i=0; i<numStates; i++)
                pi1[i] = gamma(i, 0, o, alpha, beta);

            //a的参数估计
            for(int i=0;i<numStates;i++) {
                for (int j = 0; j < numStates; j++) {
                    double num = 0;
                    double denom = 0;
                    for (int t=0;t<T-1;t++){
                        num += p(t, i, j, o, alpha, beta);
                        denom += gamma(i, t, o, alpha, beta);
                    }
                    a1[i][j] = divide(num, denom);
                }
            }

            //b的参数估计
            for (int i=0;i<numStates;i++){
                for (int k=0;k<sigmaSize;k++){
                    double num = 0;
                    double denom = 0;
                    for(int t=0; t<T-1;t++){
                        double g= gamma(i, t, o, alpha, beta);
                        num += g*(k==o[t]?1:0);
                        denom += g;
                    }
                    b1[i][k] = divide(num, denom);
                }
            }
            pi = pi1;
            a = a1;
            b = b1;
        }
    }

    public double divide(double n, double d){
        if(n==0) return 0;
        else return n/d;
    }

    //TODO 维特比解码　　测试！！！
    public int[] vertibiDecode(int[] o){
        int T = o.length;
        double[][] delta = new double[numStates][T];
        int[][] psi = new int[numStates][T];
        for(int i=0; i<numStates;i++) {
            delta[i][0] = pi[i] * b[i][o[0]];
            psi[i][0]= 0;
        }

        for(int t=1; t<T;t++){
            for(int i=0; i<numStates;i++){
                double prob= -1.0;
                for(int j=0;j<numStates;j++){
                    double nprob = delta[j][t-1] * a[j][i];
                    if(nprob >= prob){
                        prob = nprob;
                        delta[i][t] = nprob * b[i][o[t]];
                        psi[i][t] = j;
                    }
                }
            }
        }
        double pStar = -1; int iStar =-1;
        for(int i=0;i<numStates;i++){
            double p = delta[i][T-1];
            int iIndex = i;
            if (p> pStar){
                pStar = p;
                iStar = iIndex;
            }
        }
        int[] res= new int[T];
        res[T-1] = iStar;
        for(int i=T-2;i>=0;--i){
            res[i] = psi[iStar][i+1];
            iStar = psi[iStar][i+1];
        }
        return res;
    }

    public void print(){
        DecimalFormat fmt = new DecimalFormat();
        fmt.setMinimumFractionDigits(5);
        fmt.setMaximumFractionDigits(5);

        for(int i=0;i<numStates;i++)
            System.out.println("Pi(" +i+")"+fmt.format(pi[i]));
        System.out.println();
        for(int i=0;i<numStates;i++){
            for(int j=0;j<numStates;j++){
                System.out.println("a("+i+","+j+")=" + fmt.format(a[i][j])+"    ");
            }
            System.out.println();
        }

        for(int i=0;i<numStates;i++){
            for(int k=0; k<sigmaSize;k++)
                System.out.println("b(" + i + ", " + k + ")= " + fmt.format(b[i][k])+"    ");
            System.out.println();
        }
    }
}
