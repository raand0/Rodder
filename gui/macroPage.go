package gui

import (
	"MacroGo/config"
	"image/color"

	"fyne.io/fyne/v2"
	"fyne.io/fyne/v2/app"
	"fyne.io/fyne/v2/canvas"
	"fyne.io/fyne/v2/container"
	"fyne.io/fyne/v2/layout"
	hook "github.com/robotn/gohook"

	"fyne.io/fyne/v2/widget"
)

var isListening bool

func InitializeMacro() {
	myApp := app.New()
	myWindow := myApp.NewWindow("Rodder")

	//testing labels
	t_s := widget.NewLabel("swordkey is " + config.Keys.SwordKey)
	t_r := widget.NewLabel("rodkey is " + config.Keys.RodKey)
	t_m := widget.NewLabel("macrokey is " + config.Keys.MacroKey)
	t_t := widget.NewLabel("togglekey is " + config.Keys.ToggleKey)

	//row padding
	rowGap := canvas.NewRectangle(color.Transparent)
	rowGap.SetMinSize(fyne.NewSize(0, 15))

	//first row
	swordContainer := container.New(layout.NewHBoxLayout(), createPair(myApp, "Sword", &config.Keys.SwordKey, t_s))
	rodContainer := container.New(layout.NewHBoxLayout(), createPair(myApp, "Rod", &config.Keys.RodKey, t_r))
	firstRow := container.New(layout.NewHBoxLayout(), swordContainer, layout.NewSpacer(), rodContainer)

	//second row
	macroContainer := container.New(layout.NewHBoxLayout(), createPair(myApp, "Macro", &config.Keys.MacroKey, t_m))
	toggleContainer := container.New(layout.NewHBoxLayout(), createPair(myApp, "Toggle", &config.Keys.ToggleKey, t_t))
	secondRow := container.New(layout.NewHBoxLayout(), macroContainer, layout.NewSpacer(), toggleContainer)

	//checkbox
	swordCheck := widget.NewCheck("Back to sword", func(checked bool) {
		config.Keys.BackToSword = checked
	})
	thirdRow := container.New(layout.NewCenterLayout(), swordCheck)

	//pack everything
	macroPage := container.New(layout.NewVBoxLayout(), firstRow, rowGap, secondRow, rowGap, thirdRow)
	padded := container.NewPadded(macroPage)

	//footer
	footer := container.NewPadded(container.NewHBox(widget.NewLabel("rand"), layout.NewSpacer(), widget.NewLabel("version 1")))

	c := container.New(layout.NewHBoxLayout(), t_s, t_r, t_m, t_t)

	tabs := container.NewAppTabs(
		container.NewTabItem("Macro", padded),
		container.NewTabItem("How to use", c),
	)

	tabs.SetTabLocation(container.TabLocationLeading)

	final := container.New(layout.NewBorderLayout(nil, footer, nil, nil), footer, tabs)

	myWindow.SetContent(final)
	myWindow.Resize(fyne.NewSize(400, 400))
	myWindow.ShowAndRun()
}

func listen() string {
	l := hook.Start()
	defer hook.End()

	for val := range l {
		if !isListening {
			hook.End()
			return ""
		}
		if val.Kind == hook.KeyDown {
			if name, exists := config.SpecialKeys[val.Rawcode]; exists {
				return name
			}
			return hook.RawcodetoKeychar(val.Rawcode)
		}
	}

	return ""
}

func createPair(app fyne.App, label string, trackingKey *string, trackingLabel *widget.Label) fyne.CanvasObject {
	lbl := canvas.NewText(label, color.White)

	var btn *widget.Button
	btn = widget.NewButton("Select", func() {

		selectWin := app.NewWindow("press a key")
		container := container.New(layout.NewCenterLayout(), widget.NewLabel("PRESS A KEY"))
		selectWin.SetContent(container)
		selectWin.Resize(fyne.NewSize(50, 50))
		selectWin.Show()

		go func() {
			isListening = true
			selectWin.SetOnClosed(func() { isListening = false })
			key := listen()
			if key != "" {
				*trackingKey = key
				fyne.Do(func() {
					trackingLabel.SetText("swordkey is " + *trackingKey)
					btn.SetText(*trackingKey)
				})
			}
			fyne.Do(func() {
				selectWin.Close()
			})
		}()
	})

	return container.New(layout.NewHBoxLayout(), lbl, btn)
}
