package main

import (
	"fmt"
	"sync"

	"github.com/go-vgo/robotgo"
	hook "github.com/robotn/gohook"
)

var mutex sync.Mutex

func macro() {
	ch := hook.Start()
	fmt.Println("Macro is running")
	defer hook.End()

	altHeld := false

	for val := range ch {

		if val.Kind == hook.KeyDown && val.Rawcode == 164 {
			if !altHeld {
				altHeld = true
				go simulate()
			}
		}

		if val.Kind == hook.KeyUp && val.Rawcode == 164 {
			altHeld = false
			go release()
		}
	}
}

func simulate() {
	mutex.Lock()
	defer mutex.Unlock()

	robotgo.KeyTap(robotgo.KeyE)
	robotgo.Click("right")
}

func release() {
	mutex.Lock()
	defer mutex.Unlock()

	robotgo.KeyTap(robotgo.Key1)
}
