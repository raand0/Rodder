package core

import (
	"Rodder/config"
	"Rodder/shared"
	"fmt"
	"time"

	"github.com/gen2brain/beeep"
	"github.com/go-vgo/robotgo"
	hook "github.com/robotn/gohook"
)

var (
	Listeing        bool
	ListeningResult = make(chan string, 1)
	press byte = 0
	release byte = 1
	virtualGo = make(chan byte)
)

func Macro() {
	Channel := hook.Start()
	fmt.Println("Macro is running")
	defer hook.End()

	go routineOnlyVirtual()

	macroHold := false
	toggleHold := false

	for val := range Channel {

		if val.Kind == hook.KeyDown {
			if Listeing {
				var keyname string
				if name, exists := config.SpecialKeys[val.Rawcode]; exists {
					keyname = name
				} else {
					keyname = hook.RawcodetoKeychar(val.Rawcode)
				}
				Listeing = false
				ListeningResult <- keyname
				continue
			}

			if val.Rawcode == config.MacroCode {
				if shared.Switch {
					if !macroHold {
						macroHold = true
						select{
							case virtualGo <- press:
							default:
						}
					}
				} else {
					continue
				}
			} else if val.Rawcode == config.ToggleCode {
				if !toggleHold {
					toggleHold = true
					shared.SwitchHandler()
					beeep.Beep(beeep.DefaultFreq, 250)
				}
			}
		}

		if val.Kind == hook.KeyUp {
			if val.Rawcode == config.MacroCode {
				if(shared.Switch){
					if config.BackToSword {
						select{
							case virtualGo <- release:
							default:
						}
					}
					macroHold = false
				}
			}

			if val.Rawcode == config.ToggleCode {
				toggleHold = false
			}
		}
	}
}

func pressed() {

	time.Sleep(1 * time.Millisecond)
	robotgo.KeyTap(config.RodKey)
	time.Sleep(1 * time.Millisecond)
	robotgo.Click("right")
}

func released() {

	time.Sleep(1 * time.Millisecond)
	robotgo.KeyTap(config.SwordKey)
	time.Sleep(1 * time.Millisecond)
}

func Listen() string {

	Listeing = true
	keyname := <-ListeningResult
	return keyname
}

func routineOnlyVirtual(){
	for which := range virtualGo{
		switch(which){
			case press: pressed()
			case release: released()
		}
	}
}
