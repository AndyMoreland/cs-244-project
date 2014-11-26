package server;

import org.apache.thrift.TException;
import tutorial.Calculator;
import tutorial.InvalidOperation;
import tutorial.Work;

/**
 * Created by andrew on 11/25/14.
 */
public class CalculatorHandler implements Calculator.Iface {
    @Override
    public void ping() throws TException {
        System.out.println("Ping! Hello!");
    }

    @Override
    public int add(int num1, int num2) throws TException {
        return 5;
    }

    @Override
    public int calculate(int logid, Work w) throws InvalidOperation, TException {
        return 0;
    }

    @Override
    public void zip() throws TException {

    }
}
