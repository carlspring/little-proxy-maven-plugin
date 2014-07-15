package org.carlspring.maven.littleproxy.server;

import org.carlspring.maven.littleproxy.mojo.AbstractLittleProxyMojo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mtodorov
 */
public class ShutdownServer
{

    private static Logger logger = LoggerFactory.getLogger(ShutdownServer.class);

    private int port = 8181;

    private String shutdownHash;


    public static void main(String[] args)
    {
        new ShutdownServer().startServer();
    }

    public void startServer()
    {
        final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(3);

        Runnable serverTask = new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    ServerSocket serverSocket = new ServerSocket(getPort());

                    logger.info("Accepting shutdown requests on port " + getPort() +  "...");

                    //noinspection InfiniteLoopStatement
                    while (true)
                    {
                        Socket clientSocket = serverSocket.accept();
                        clientProcessingPool.submit(new ClientTask(clientSocket));
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        };

        Thread serverThread = new Thread(serverTask);
        serverThread.start();
    }

    private class ClientTask
            implements Runnable
    {

        private final Socket clientSocket;

        private ClientTask(Socket clientSocket)
        {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run()
        {
            logger.debug("Accepted client connection from " + clientSocket.getInetAddress().getHostAddress() + ".");

            BufferedReader br = null;

            try
            {
                final OutputStream os = clientSocket.getOutputStream();
                br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                final String hash = br.readLine();

                logger.debug("Received hash: " + hash);
                logger.debug("Expected hash: " + shutdownHash);

                if (hash.equals(shutdownHash))
                {
                    AbstractLittleProxyMojo.getProxyServer().stop();

                    os.write("Server shutdown successfully.\n".getBytes());
                    os.flush();
                    os.close();
                }
                else
                {
                    os.write("Forbidden.\n".getBytes());
                    os.flush();
                    os.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                if (br != null)
                {
                    try
                    {
                        br.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                if (clientSocket != null)
                {
                    try
                    {
                        clientSocket.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public String getShutdownHash()
    {
        return shutdownHash;
    }

    public void setShutdownHash(String shutdownHash)
    {
        this.shutdownHash = shutdownHash;
    }

}