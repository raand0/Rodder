package config

var SwordKey string
var RodKey string
var BackToSword bool
var ToggleCode uint16
var MacroCode uint16
var MacroKey string
var ToggleKey string

var SpecialKeys = map[uint16]string{
	27:  "esc",
	192: "`",
	9:   "tab",
	20:  "capslock",
	160: "lshift",
	162: "lctrl",
	164: "lalt",
	32:  "space",
	165: "ralt",
	189: "-",
	187: "=",
	219: "[",
	221: "]",
	186: ";",
	222: "'",
	188: ",",
	190: ".",
	191: "/",
	163: "rctrl",
	8:   "backspace",
	44:  "printscreen",
	220: "\\",
	13:  "enter",
	161: "rshift",
	33:  "pageup",
	38:  "up",
	34:  "pagedown",
	37:  "left",
	40:  "down",
	39:  "right",
	46:  "delete",
}
