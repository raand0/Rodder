package shared

import (
	"fyne.io/fyne/v2"
	"fyne.io/fyne/v2/widget"
)

var Switch = false
var SharedBtn *widget.Button

func SwitchHandler() {
	fyne.Do(func() {
		if SharedBtn != nil {
			if !Switch {
				Switch = true
				SharedBtn.SetText("ON")
				SharedBtn.Importance = widget.HighImportance
			} else {
				Switch = false
				SharedBtn.SetText("OFF")
				SharedBtn.Importance = widget.DangerImportance
			}
			SharedBtn.Refresh()
		}
	})
}
