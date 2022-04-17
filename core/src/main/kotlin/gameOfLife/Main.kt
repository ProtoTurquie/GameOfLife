package gameOfLife

import com.badlogic.gdx.Game


/** [com.badlogic.gdx.ApplicationListener] implementation shared by all platforms.  */
class Main : Game {
    constructor(platformSpecific: PlatformSpecific) : super()
    {
        platformcode = platformSpecific
    }
    override fun create() {
        centralScreen = MainScreen()
        setScreen(centralScreen)
    }
}