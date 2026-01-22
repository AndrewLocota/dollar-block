# 🚀 Build Instructions for Android Studio

## Quick Start

1. **Open the project in Android Studio**
   - Launch Android Studio
   - Click "Open" and select the `dollar-block` folder
   - Wait for Gradle sync to complete (may take a few minutes first time)

2. **Run on Emulator**
   - Click the device dropdown at the top
   - Select "Create Device" if you don't have an emulator
   - Choose a device (Pixel 5 recommended)
   - Select system image: **API 34 (Android 14)** or higher
   - Click "Finish" then "Run" (green play button)

3. **Run on Real Device**
   - Enable Developer Mode on your Android phone:
     - Go to Settings → About Phone
     - Tap "Build Number" 7 times
     - Go back, enter Settings → Developer Options
     - Enable "USB Debugging"
   - Connect phone via USB
   - Select your device from the dropdown
   - Click "Run" (green play button)

## Enable App Blocking

After installing the app:

1. Open Dollar Block
2. Complete onboarding
3. Select apps to block
4. Tap Settings icon (top right)
5. Find "Dollar Block" in Accessibility Services
6. Toggle it ON
7. Confirm permission

## Testing the App Flow

### Flow 1: Onboarding
1. Launch app → See "Discipline is cheap. Distraction costs $1."
2. Click "Link Card & Start Blocking →"
3. Goes to app selection

### Flow 2: App Selection
1. See "Choose your poison." screen
2. Toggle switches to block apps (e.g., TikTok, Instagram)
3. Click "DONE"

### Flow 3: Try to Open Blocked App
1. Enable accessibility service (see above)
2. Press home button
3. Try to open a blocked app (e.g., Instagram)
4. See full-screen block: "We want you to pay.\nGive in."
5. Options:
   - Click "Pay $1.00 to Unblock" → See shame screen
   - Click "I'LL STAY FOCUSED" → Returns to home

### Flow 4: Shame Screen (After "Paying")
1. Button shows loading spinner
2. Then shows: "Weak.\nPREDICTABLE OUTCOME."
3. White checkmark appears
4. "-$1.00 deducted."
5. Progress bar: "UNLOCKING INSTAGRAM..."
6. Countdown timer: "Redirecting in 25s → 0s"
7. Auto-closes and app unblocks

## Project Structure

```
dollar-block/
├── app/
│   ├── build.gradle.kts          # Dependencies
│   └── src/main/
│       ├── AndroidManifest.xml   # App config
│       ├── java/com/dollarblock/app/
│       │   ├── OnboardingActivity.kt    # Screen 1: "Discipline is cheap"
│       │   ├── MainActivity.kt          # Screen 2: "Choose your poison"
│       │   ├── UnblockActivity.kt       # Screen 3 & 4: Block & Shame
│       │   ├── data/                    # Database models
│       │   ├── repository/              # Data layer
│       │   ├── service/                 # Background service
│       │   ├── ui/theme/                # Colors & theme
│       │   └── viewmodel/               # ViewModels
│       └── res/                         # Resources (layouts, strings)
├── build.gradle.kts              # Project config
└── settings.gradle.kts           # Gradle settings
```

## Key Features Implemented

✅ Onboarding screen with philosophy
✅ App selection with toggle switches
✅ Full black UI with serif fonts
✅ Pre-payment taunt screen
✅ Post-payment shame screen
✅ Progress bar with countdown
✅ AccessibilityService for real-time blocking
✅ Room database for persistence
❌ No payment processing (intentionally simplified)

## Troubleshooting

### Gradle Sync Failed
- **Fix**: File → Invalidate Caches → Restart

### App Crashes on Launch
- **Check**: Minimum SDK is 24 (Android 7.0)
- **Check**: Target SDK is 34 (Android 14)

### Accessibility Service Not Working
- **Fix**: Go to Settings → Accessibility → Dollar Block → Toggle OFF then ON

### Can't Find App in Launcher
- **Fix**: Check AndroidManifest.xml has correct launcher activity (OnboardingActivity)

### Emulator is Slow
- **Fix**: In AVD Manager, edit device → Show Advanced Settings → Graphics: "Hardware - GLES 2.0"

## Making Changes

### To modify UI:
- **Onboarding**: Edit `OnboardingActivity.kt`
- **App Selection**: Edit `MainActivity.kt`
- **Block Screen**: Edit `UnblockActivity.kt` (lines 150-270)
- **Shame Screen**: Edit `UnblockActivity.kt` (lines 270-420)

### To modify colors:
- Edit `app/src/main/java/com/dollarblock/app/ui/theme/Color.kt`

### To modify strings:
- Edit `app/src/main/res/values/strings.xml`

### To add new taunt messages:
- Edit `UnblockActivity.kt` lines 96-109 (pre-payment)
- Edit `UnblockActivity.kt` lines 114-124 (post-payment)

## Building APK for Testing

1. **Debug APK** (for testing):
   ```
   Build → Build Bundle(s) / APK(s) → Build APK(s)
   ```
   APK location: `app/build/outputs/apk/debug/app-debug.apk`

2. **Release APK** (for distribution):
   ```
   Build → Generate Signed Bundle / APK → APK
   ```
   (Requires signing key)

## Requirements

- **Android Studio**: Hedgehog (2023.1.1) or newer
- **Gradle**: 8.2+
- **JDK**: 17
- **Android SDK**: 34 (Android 14)
- **Minimum Device**: Android 7.0 (API 24)

## Notes

- No payment processing is implemented - the "Pay" button simulates success
- The app is intentionally simplified for easy testing and modification
- All UI matches the provided mockups exactly
- Serif fonts (FontFamily.Serif) are used throughout for the sophisticated look
