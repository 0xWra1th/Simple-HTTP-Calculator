/* 
TODO:
    1) Convert string equation into answer
    2) Allow for multiple users...maybe
    3) Add a clear button?
    4) Add a memory function?
    5) Add brackets?
    6) Add more headers so I look like I know what I'm doing.
*/
//IMPORTS
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server{

    // ------ GLOBAL VARIABLES ------
    private static ServerSocket serverSocket;
    private static int port;
    private static boolean running = false;
    private static String ANSWER = "";
    private static String MEMANS = "";
    // ------------------------------

    public static void main(String[] args){
        //Initialise variables
        port = Integer.parseInt(args[0]);

        //Run Server
        setupServer();
        serverLoop();
    }

    private static void serveWebsite(Socket sock){
        // ---------- CONVERT WEBSITE WITH NEW ANSWER TO STRING ----------
        String res =    "HTTP/1.1 200 OK\nContent-Type: text/html\n\n"+
                        "<html style=\"background-color: #363636;\" lang=\"en\">"+"\n"+
                        "<head>"+"\n"+
                        "<title>Calculator</title>"+"\n"+
                        "</head>"+"\n"+
                        "<body>"+"\n"+
                        "<center>"+"\n"+
                        "<textarea readonly style=\"margin-top: 100px;height: 50px; width:160px; font-size: 15px;resize: none;\">"+ANSWER+"</textarea><br/>"+"\n"+
                        "<a href=\"1\"><button style=\"margin-top:5px;height: 50px; width:50px; font-size: 20px;\">1</button></a>"+"\n"+
                        "<a href=\"2\"><button style=\"margin-left:5px;margin-top:5px;height: 50px; width:50px; font-size: 20px;\">2</button></a>"+"\n"+
                        "<a href=\"3\"><button style=\"margin-left:5px;margin-top:5px;height: 50px; width:50px; font-size: 20px;\">3</button></a><br/>"+"\n"+
                        "<a href=\"4\"><button style=\"margin-top:5px;height: 50px; width:50px; font-size: 20px;\">4</button></a>"+"\n"+
                        "<a href=\"5\"><button style=\"margin-left:5px;margin-top:5px;height: 50px; width:50px; font-size: 20px;\">5</button></a>"+"\n"+
                        "<a href=\"6\"><button style=\"margin-left:5px;margin-top:5px;height: 50px; width:50px; font-size: 20px;\">6</button></a><br/>"+"\n"+
                        "<a href=\"7\"><button style=\"margin-top:5px;height: 50px; width:50px; font-size: 20px;\">7</button></a>"+"\n"+
                        "<a href=\"8\"><button style=\"margin-left:5px;margin-top:5px;height: 50px; width:50px; font-size: 20px;\">8</button></a>"+"\n"+
                        "<a href=\"9\"><button style=\"margin-left:5px;margin-top:5px;height: 50px; width:50px; font-size: 20px;\">9</button></a><br/>"+"\n"+
                        "<a href=\"add\"><button style=\"margin-top:5px;height: 50px; width:50px; font-size: 20px;\">+</button></a>"+"\n"+
                        "<a href=\"0\"><button style=\"margin-left:5px;margin-top:5px;height: 50px; width:50px; font-size: 20px;\">0</button></a>"+"\n"+
                        "<a href=\"sub\"><button style=\"margin-left:5px;margin-top:5px;height: 50px; width:50px; font-size: 20px;\">-</button></a><br/>"+"\n"+
                        "<a href=\"mult\"><button style=\"margin-top:5px;height: 50px; width:50px; font-size: 20px;\">x</button></a>"+"\n"+
                        "<a href=\"div\"><button style=\"margin-left:5px;margin-top:5px;height: 50px; width:50px; font-size: 20px;\">/</button></a>"+"\n"+
                        "<a href=\"eq\"><button style=\"margin-left:5px;margin-top:5px;height: 50px; width:50px; font-size: 20px;\">=</button></a><br/>"+"\n"+
                        "<a href=\"/\"><button style=\"margin-top:5px;height: 50px; width:90px; font-size: 20px;\">CLEAR</button></a>"+"\n"+
                        "<a href=\"mem\"><button style=\"margin-left:5px;margin-top:5px;height: 50px; width:70px; font-size: 20px;\">MEM</button></a>"+"\n"+
                        "</center>"+"\n"+
                        "</body>"+"\n"+
                        "</html>";
        // ---------------------------------------------------------------
        // ----------- SEND WEBSITE IN HTTP RESPONSE TO CLIENT -----------
        try{
            PrintWriter out = new PrintWriter(sock.getOutputStream());
            out.print(res);
            out.flush();
        }catch(Exception e){
            e.printStackTrace();
        }
        // ---------------------------------------------------------------
    }

    private static void serverLoop(){
        // ----------- WAIT FOR CONNECTION, READ REQUESTS AND HANDLE THEM -----------
        while(running){
            try{
                Socket socket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                
                // -------- Handle Request --------
                handleRequest(in, socket);
                // --------------------------------
                
            }catch(Exception e){
                running = false;
                e.printStackTrace();
            }
        }
        // --------------------------------------------------------------------------
    }

    private static void setupServer(){
        // ----------- CREATE SERVER SOCKETS AND BIND TO PORT -----------
        try{
            serverSocket = new ServerSocket(port);
            running = true;
        }catch(Exception e){
            e.printStackTrace();
        }
        
        if(running){
            System.out.println("Server running on port: "+port);
        }else{
            System.out.println("ERROR: Server failed to start on port: "+port+"!");
        }
        // --------------------------------------------------------------
    }

    //helper function
    private static boolean isOperant(String input){
        if(input.equals("+") || input.equals("/") || input.equals("-") || input.equals("x")){
            return true;
        }else if(input.equals("add") || input.equals("div") || input.equals("sub") || input.equals("mult")){
            return true;
        }
        return false;
    }
    private static String getOperant(String input){
        if(input.equals("sub")){
            return "-";
        }else if(input.equals("mult")){
            return "x";
        }else if(input.equals("div")){
            return "/";
        }else if(input.equals("add")){
            return "+";
        }
        return null;
    }

    //function for creating the string of calculation operators
    private static void handleInput(String input){
        //exceptions: 01, +*, 
        int lengthOfAnswer = ANSWER.length();
        try{
            if(isOperant(input)){
                //check that operator is not the first input
                if(lengthOfAnswer == 1){
                    ANSWER += getOperant(input);
                }else if(lengthOfAnswer >= 2){
                    //get substrings 
                    String sub1 = ANSWER.substring(lengthOfAnswer-1);
                    String sub2 = ANSWER.substring(lengthOfAnswer-2, lengthOfAnswer-1);

                    if(input.equals("sub")){
                        System.out.println(sub1);
                        System.out.println(sub2);
                        if(!isOperant(sub2) && isOperant(sub1)){
                            ANSWER += getOperant(input);
                        }else if(isOperant(sub2) && !isOperant(sub1)){
                            ANSWER += getOperant(input);
                        } else if(!isOperant(sub2) && !isOperant(sub1)){
                            ANSWER += getOperant(input);
                        }
                    }else{
                        if(!isOperant(sub1)){
                            ANSWER += getOperant(input);
                        }
                    }
                }
            }else if(input.equals("mem")){
                ANSWER += MEMANS;
            }else{
                if(lengthOfAnswer >= 2){
                    String sub1 = ANSWER.substring(lengthOfAnswer-1);
                    String sub2 = ANSWER.substring(lengthOfAnswer-2, lengthOfAnswer-1);

                    if(isOperant(sub1) && !input.equals("0")){
                        ANSWER += input;
                    }else if(!isOperant(sub1)){
                        ANSWER += input;
                    }
                }else{
                    if(!input.equals("0")){
                        ANSWER += input;
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static void handleRequest(BufferedReader in, Socket sock){
        // ------------ READ THROUGH HEADERS AND DETERMINE ACTIONS ------------
        try{
            String line = in.readLine();
            while(line != null && line.length() > 0){
                String method = line.split(" ")[0];
                String url = line.split(" ")[1];
                if(method.equals("GET")){
                    if(url.equals("/favicon.ico")){ //IGNORE FAVICON REQUESTS...
                        break;
                    }else if(url.equals("/0")){
                        System.out.println("0");
                        handleInput("0");
                        serveWebsite(sock);
                        break;
                    }else if(url.equals("/1")){
                        System.out.println("1");
                        handleInput("1");
                        serveWebsite(sock);
                        break;
                    }else if(url.equals("/2")){
                        System.out.println("2");
                        handleInput("2");
                        serveWebsite(sock);
                        break;
                    }else if(url.equals("/3")){
                        System.out.println("3");
                        handleInput("3");
                        serveWebsite(sock);
                        break;
                    }else if(url.equals("/4")){
                        System.out.println("4");
                        handleInput("4");
                        serveWebsite(sock);
                        break;
                    }else if(url.equals("/5")){
                        System.out.println("5");
                        handleInput("5");
                        serveWebsite(sock);
                        break;
                    }else if(url.equals("/6")){
                        System.out.println("6");
                        handleInput("6");
                        serveWebsite(sock);
                        break;
                    }else if(url.equals("/7")){
                        System.out.println("7");
                        handleInput("7");
                        serveWebsite(sock);
                        break;
                    }else if(url.equals("/8")){
                        System.out.println("8");
                        handleInput("8");
                        serveWebsite(sock);
                        break;
                    }else if(url.equals("/9")){
                        System.out.println("9");
                        handleInput("9");
                        serveWebsite(sock);
                        break;
                    }else if(url.equals("/add")){
                        System.out.println("add");
                        handleInput("add");
                        serveWebsite(sock);
                        break;
                    }else if(url.equals("/sub")){
                        System.out.println("sub");
                        handleInput("sub");
                        serveWebsite(sock);
                        break;
                    }else if(url.equals("/mult")){
                        System.out.println("mult");
                        handleInput("mult");
                        serveWebsite(sock);
                        break;
                    }else if(url.equals("/div")){
                        System.out.println("div");
                        handleInput("div");
                        serveWebsite(sock);
                        break;
                    }else if(url.equals("/")){
                        System.out.println("RESET");
                        ANSWER = "";
                        serveWebsite(sock);
                        break;
                    }else if(url.equals("/mem")){
                        System.out.println("mem");
                        handleInput("mem");
                        serveWebsite(sock);
                    }else if(url.equals("/eq")){
                        System.out.println("equals");
                        ANSWER = calcAnswer();
                        serveWebsite(sock);
                        break;
                    }
                    System.out.println(line);
                    line = in.readLine();
                }else if(method.equals("HEAD")){
                    // SEND SERVER INFORMATION IN HEADERS
                    handleHead(sock);
                    break;
                }else if(method.equals("OPTIONS")){
                    // SEND AVAILABLE HTTP METHODS IN HEADERS
                    handleOptions(sock);
                    break;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        // --------------------------------------------------------------------
    }

    private static String calcAnswer(){
        // ------------ CONVERT STRING EQUATION INTO ANSWER ------------
        String res = "The Answer";
        return res;
        // -------------------------------------------------------------
    }

    private static void handleHead(Socket s){
    	// ----------- SEND HEADERS IN HTTP RESPONSE TO CLIENT -----------
    	String res = 	"HTTP/1.1 200 OK"+"\n"+
    					"Server: HTTP Calculator"+"\n"+
    					"Host: localhost:"+port+"\n"+
    					"X-Powered-By: Java\n\n";
        try{
            PrintWriter out = new PrintWriter(s.getOutputStream());
            out.print(res);
            out.flush();
        }catch(Exception e){
            e.printStackTrace();
        }
        // ---------------------------------------------------------------
    }

    private static void handleOptions(Socket s){
    	// ----------- SEND OPTIONS IN HTTP RESPONSE TO CLIENT -----------
    	String res = 	"HTTP/1.1 200 OK"+"\n"+
    					"Allow: GET HEAD OPTIONS"+"\n"+
    					"Server: HTTP Calculator"+"\n"+
    					"Host: localhost:"+port+"\n"+
    					"X-Powered-By: Java\n\n";
        try{
            PrintWriter out = new PrintWriter(s.getOutputStream());
            out.print(res);
            out.flush();
        }catch(Exception e){
            e.printStackTrace();
        }
        // ---------------------------------------------------------------
    }

}
















