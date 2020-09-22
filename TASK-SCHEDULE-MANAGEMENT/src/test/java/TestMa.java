
public class TestMa {
    public static void main(String[] args) throws Exception {

        try{
            String s = null;
            new Thread(()->{
                try {
                    Thread.sleep(100*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            System.out.println("main");
        }catch (Exception e){
            System.out.println("exception");
        }finally {
            System.out.println("finally");
        }
    }
}
