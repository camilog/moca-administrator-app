package camilog.adminapp.elections;

import android.util.Log;

import java.math.BigInteger;

/**
 * Created by stefano on 02-09-15.
 */
public class ElectionResults {
    BigInteger finalResultBigInteger;
    int numberOfCandidates; // Doesn't consider blank vote
    String[] candidatesNames;
    byte[] finalResultByteArray;
    int[] finalResultIntArray;

    public ElectionResults(BigInteger finalResultBigInteger, int numberOfCandidates) {
        this.finalResultBigInteger = finalResultBigInteger;
        this.numberOfCandidates = numberOfCandidates;
        this.finalResultByteArray = this.finalResultBigInteger.toByteArray();
        Log.i("jiji", "A");
        generateIntArray();
        Log.i("jiji", "B");
    }

    private void generateIntArray() {
        this.finalResultIntArray = new int[numberOfCandidates + 1];
        for (int i = this.finalResultByteArray.length - 1, c = finalResultIntArray.length - 1; i >= 0; i = i-4, c--) {
            Log.i("jiji", "i = " + String.valueOf(i));
            for (int j = i;  j > i-4 && j >= 0; j--) {
                Log.i("jiji", "j = " + String.valueOf(j));
                byte b = this.finalResultByteArray[j];
                int a = b & 0xff;
                this.finalResultIntArray[c] = this.finalResultIntArray[c] + (int) (a*Math.pow(2,(i-j)*8));
            }
        }
    }

    public int[] getResultsByCandidate() {
        return this.finalResultIntArray;
    }

    public byte[] getResultsByteArray() {
        return this.finalResultByteArray;
    }
}
