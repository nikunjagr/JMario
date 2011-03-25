package infraestrutura.input;

/**
 * A classe GameAction � uma abstra��o para uma a��o iniciada pelo usu�rio,
 * como pular ou mover. As GameActions podem ser mapeadas para teclas ou
 * mouse usando o InputManager.
 *
 * @author David Buzatto
 */
public class GameAction {
    
    /**
     * Comportamento normal. O m�todo isPressed() retorna true quando a tecla
     * � mantida pressionada.
     */
    public static final int NORMAL = 0;
    
    /**
     * Comportamento de pressionamento inicial. O m�todo isPressed() retorna
     * true somente depois que a tecla � pressionada pela primeira vez, e n�o
     * novamente ate que a tecla seja solta e pressionada novamente.
     */
    public static final int DETECT_INITAL_PRESS_ONLY = 1;
    
    private static final int STATE_RELEASED = 0;
    private static final int STATE_PRESSED = 1;
    private static final int STATE_WAITING_FOR_RELEASE = 2;
    
    private String name;
    private int behavior;
    private int amount;
    private int state;
    
    
    /**
     * Cria uma nova GameAction com comportamento NORMAL.
     */
    public GameAction( String name ) {
        this( name, NORMAL );
    }
    
    
    /**
     * Cria uma nova GameAction com o comportamento especificado.
     */
    public GameAction( String name, int behavior ) {
        this.name = name;
        this.behavior = behavior;
        reset();
    }
    
    
    /**
     * Obt�m o nome dessa GameAction.
     */
    public String getName() {
        return name;
    }
    
    
    /**
     * Reseta esta GameAction, fazendo parecer que esta n�o foi pressionada.
     */
    public void reset() {
        state = STATE_RELEASED;
        amount = 0;
    }
    
    
    /**
     * Pressionamento r�pido para essa GameAction. O mesmo que chamar press()
     * seguido de release().
     */
    public synchronized void tap() {
        press();
        release();
    }
    
    
    /**
     * Sinaliza que a tecla foi pressionada.
     */
    public synchronized void press() {
        press( 1 );
    }
    
    
    /**
     * Sinaliza que a tecla foi pressionada na quantidade de vezes especificada,
     * ou que o mouse se moveu numa dist�ncia especificada.
     */
    public synchronized void press( int amount ) {
        if ( state != STATE_WAITING_FOR_RELEASE ) {
            this.amount += amount;
            state = STATE_PRESSED;
        }
    }
    
    
    /**
     * Sinaliza que a tecla foi solta.
     */
    public synchronized void release() {
        state = STATE_RELEASED;
    }
    
    
    /**
     * Retorna se a tecla foi pressionada ou n�o desde a �ltima checagem.
     */
    public synchronized boolean isPressed() {
        return ( getAmount() != 0 );
    }
    
    
    /**
     * Para teclas, � a quantidade de vezes que a tecla foi pressionada desde
     * a �ltima vez que foi checada.
     * Para eventos do mouse, � a dist�ncia que cursor foi movido.
     */
    public synchronized int getAmount() {
        int retVal = amount;
        if ( retVal != 0 ) {
            if ( state == STATE_RELEASED ) {
                amount = 0;
            } else if ( behavior == DETECT_INITAL_PRESS_ONLY ) {
                state = STATE_WAITING_FOR_RELEASE;
                amount = 0;
            }
        }
        return retVal;
    }
    
}