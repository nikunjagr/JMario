package jogo.tile;

import infraestrutura.grafico.*;
import java.awt.*;
import java.util.Iterator;
import javax.swing.JFrame;
import jogo.sprites.*;

/**
 * A classe TileMapRenderer desenha um TileMap na tela.
 * Ela desenha todos os tiles, sprites, e o a imagem de fundo opcional, 
 * centralizados na posi��o do jogador.
 *
 * <p>Se a largura da imagem de fundo por menor que a largura do mapa, a 
 * imagem de fundo parecer� que esta se movendo devagar, criando o 
 * efeito de parallax.
 *
 * <p>Tamb�m, tr�s m�todos est�ticos s�o fornecidos para converter pixels em 
 * posi��es dos tiles e vice-versa.
 *
 * <p>Esse TileMapRender usa tiles com tamanho de 64.
 */
public class TileMapRenderer {
    
    private static final int TILE_SIZE = 32;
    
    // o tamanho em bits do tile
    // Math.pow( 2, TILE_SIZE_BITS ) == TILE_SIZE
    private static final int TILE_SIZE_BITS = 5;
    
    private Image background;
    
    /**
     * Converte uma posi��o em pixel para a posi��o de um tile.
     */
    public static int pixelsToTiles( float pixels ) {
        return pixelsToTiles( Math.round( pixels ) );
    }
    
    
    /**
     * Converte uma posi��o em pixel para a posi��o de um tile.
     */
    public static int pixelsToTiles(int pixels) {
        // usa deslocamento para obter os valores corretos para pixels negativos
        return pixels >> TILE_SIZE_BITS;
        
        // ou, se o tamanho dos tiles n�o forem pot�ncia de dois, usa o m�todo 
        // floor():
        // return ( int ) Math.floor( ( float ) pixels / TILE_SIZE );
    }
    
    
    /**
     * Converte a posi��o de um tile para a posi��o em pixel.
     */
    public static int tilesToPixels(int numTiles) {
        // sem raz�o real para usar deslocamento aqui.
        // o seu uso � um pouco mais r�pido, mas nos processadores modernos isso 
        // quase n�o faz diferen�a
        return numTiles << TILE_SIZE_BITS;
        
        // se o tamanho dos tiles n�o forem pot�ncia de dois,
        // return numTiles * TILE_SIZE;
    }
    
    
    /**
     * Configura o fundo para desenhar.
     */
    public void setBackground( Image background ) {
        this.background = background;
    }
    
    
    /**
     * Desenha o TileMap especificado.
     */
    public void draw( Graphics2D g, TileMap map,
            int screenWidth, int screenHeight ) {
        
        Sprite player = map.getPlayer();
        int mapWidth = tilesToPixels( map.getWidth() );
        
        // obt�m a posi��o de scrolling do mapa, baseado na posi��o do jogador
        int offsetX = screenWidth / 2 -
                Math.round( player.getX() ) - TILE_SIZE;
        offsetX = Math.min( offsetX, 0 );
        offsetX = Math.max( offsetX, screenWidth - mapWidth );
        
        // obt�m o offset de y para desenhar todas as sprites e tiles
        int offsetY = screenHeight -
                tilesToPixels( map.getHeight() );
        
        // desenha um fundo preto se necess�rio
        if ( background == null ||
                screenHeight > background.getHeight( null ) ) {
            g.setColor( Color.BLACK );
            g.fillRect( 0, 0, screenWidth, screenHeight );
        }
        
        // desenha a imagem de fundo usando parallax
        if ( background != null ) {
            int x = offsetX *
                    ( screenWidth - background.getWidth( null ) ) /
                    ( screenWidth - mapWidth );
            int y = screenHeight - background.getHeight( null );
            
            g.drawImage( background, x, y, null );
        }
        
        // desenha os tiles vis�veis
        int firstTileX = pixelsToTiles( -offsetX );
        int lastTileX = firstTileX +
                pixelsToTiles( screenWidth ) + 1;
        for ( int y = 0; y < map.getHeight(); y++ ) {
            for ( int x = firstTileX; x <= lastTileX; x++ ) {
                Image image = map.getTile( x, y );
                if ( image != null ) {
                    g.drawImage( image,
                            tilesToPixels( x ) + offsetX,
                            tilesToPixels( y ) + offsetY,
                            null );
                }
            }
        }
        
        // desenha o jogador
        g.drawImage( player.getImage(),
                Math.round( player.getX() ) + offsetX,
                Math.round( player.getY() ) + offsetY,
                null );
        
        // desenha as sprites
        Iterator i = map.getSprites();
        while ( i.hasNext() ) {
            Sprite sprite = ( Sprite ) i.next();
            int x = Math.round( sprite.getX() ) + offsetX;
            int y = Math.round( sprite.getY() ) + offsetY;
            g.drawImage( sprite.getImage(), x, y, null );
            
            // acorda a critura quando a mesma estiver na tela
            if ( sprite instanceof Creature &&
                    x >= 0 && x < screenWidth ) {
                ( ( Creature ) sprite ).wakeUp();
            }
        }
        
    }
    
}