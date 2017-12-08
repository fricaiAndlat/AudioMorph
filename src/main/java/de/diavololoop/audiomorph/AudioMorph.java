package de.diavololoop.audiomorph;

import org.jtransforms.fft.DoubleFFT_1D;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.io.*;
import java.nio.file.Files;

/**
 * Created by Chloroplast on 07.12.2017.
 */
public class AudioMorph {

//1420 - 1630
    public static void main(String[] args) throws IOException, LineUnavailableException {


        //read input file
        byte[] file = Files.readAllBytes(new File("F:\\Musik\\Linkin Park\\Burn It Down [EP]\\01 Burn It Down.wav").toPath());



        double[][] data = new double[2][file.length / 4];

        System.out.println("raw to double");

        //raw to double (ignoring the metadata)
        for (int i = 0; i < file.length; i+=2) {

            int d = (file[i+1]<<8) | file[i]&0xFF;
            data[(i/2) % 2][i/4] = d / (double)Short.MAX_VALUE;


        }

        //FX: to frq
        System.out.println("FX: to frq");
        DoubleFFT_1D fft = new DoubleFFT_1D(data[0].length);
        fft.realForward(data[0]);
        fft.realForward(data[1]);

        //FX do stuff
        System.out.println("crazyness");

        for(int i = 0; i < data[0].length; ++i) {
            data[0][i] = data[0][i]*0.6;
            data[1][i] = data[1][i]*0.6;
        }


        int off = 20000;
        for(int i = off; i < data[0].length; ++i) {
            data[0][i-off] = data[0][i];
            data[1][i-off] = data[1][i];
        }
        for(int i = data[0].length-off; i < data[0].length; ++i) {
            data[0][i-off] = 0;
            data[1][i-off] = 0;
        }

        //FX: to frq
        System.out.println("FX: from frq");
        fft.realInverse(data[0], true);
        fft.realInverse(data[1], true);


        //double to raw;
        System.out.println("double to raw");
        for (int i = 0; i < file.length; i+=2) {

            int d = (int)(data[(i/2) % 2][i/4] * Short.MAX_VALUE);
            file[i+1] = (byte)(d>>8);
            file[i] = (byte)(d);
        }


        System.out.println("save to file");
        File outputFile = new File("output.wav");
        outputFile.createNewFile();
        FileOutputStream fis = new FileOutputStream(outputFile);
        fis.write(file, 0, file.length);
        fis.close();

        System.out.println("play");
        AudioFormat af = new AudioFormat(44100, 16, 2, true, false);

        SourceDataLine out = AudioSystem.getSourceDataLine(af);
        out.open(af, file.length);
        out.start();

        out.write(file, 0, file.length);

        out.drain();





    }


}
