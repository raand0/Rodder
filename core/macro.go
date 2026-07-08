package core

import (
	"MacroGo/config"
	"MacroGo/shared"
	"fmt"
	"sync"

	"github.com/go-vgo/robotgo"
	hook "github.com/robotn/gohook"
)

var mutex sync.Mutex

var (
	Listeing        bool
	ListeningResult = make(chan string, 1)
)

var SwordKey string
var RodKey string
var BackToSword bool
var ToggleCode uint16
var MacroCode uint16

var Channel = hook.Start()

func Macro() {
	fmt.Println("Macro is running")
	defer hook.End()

	macroHold := false
	toggleHold := false

	for val := range Channel {

		if val.Kind == hook.KeyDown {
			fmt.Println("Value: ", val.Rawcode)
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

			if val.Rawcode == MacroCode {
				fmt.Println("Macro key pressed")
				if !macroHold {
					macroHold = true
					go simulate()
				}
			} else if val.Rawcode == ToggleCode {
				fmt.Println("Toggle key pressed")
				if !toggleHold {
					toggleHold = true
					shared.SwitchHandler()
				}
			}
		}

		if val.Kind == hook.KeyUp {
			if val.Rawcode == MacroCode {
				if BackToSword {
					go release()
				}
				macroHold = false
			}

			if val.Rawcode == ToggleCode {
				toggleHold = false
			}
		}
	}
}

func simulate() {
	mutex.Lock()
	defer mutex.Unlock()

	robotgo.KeyTap(RodKey)
	robotgo.Click("right")
}

func release() {
	mutex.Lock()
	defer mutex.Unlock()

	robotgo.KeyTap(SwordKey)
}

func Listen() string {

	Listeing = true
	keyname := <-ListeningResult
	return keyname
}
