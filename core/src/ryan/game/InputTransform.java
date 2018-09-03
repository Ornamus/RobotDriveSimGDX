package ryan.game;

public class InputTransform
{
    private static int appWidth = (int)Main.screenWidth;
    private static int appHeight = (int)Main.screenHeight;

    public static float getCursorToModelX(int screenX, int cursorX)
    {
        return (((float)cursorX) * appWidth) / ((float)screenX);
    }

    public static float getCursorToModelY(int screenY, int cursorY)
    {
        return ((float)(screenY - cursorY)) * appHeight / ((float)screenY) ;
    }
}