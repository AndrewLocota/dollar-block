# 💵 Dollar Block

**The simplest, most effective screen time blocker for Android**

Block distracting apps and pay $1 each time you want to unblock them. Every dollar you pay goes to charity, turning your distractions into donations.

## ✨ Features

- **Super Simple**: Toggle switches to block apps. That's it.
- **$1 to Unblock**: When you try to open a blocked app, pay $1 to unlock it
- **Sophisticated Taunts**: Elegant serif typography with devastating messages ("We want you to pay.\nGive in.", "Your future self is watching.")
- **Full Black UI**: Minimalist design with serif fonts - looks like a luxury product
- **Real-time Blocking**: Uses Android AccessibilityService for instant app detection
- **Post-Payment Shame**: Progress bar showing "UNLOCKING..." with countdown timer
- **Stripe Integration**: Secure payment processing (test mode ready)

## 🎯 How It Works

1. **Choose Your Poison**: Select apps to block with elegant toggle switches
2. **Get Interrupted**: Try to open a blocked app? Full-screen black overlay appears
3. **See The Taunt**: Large serif message: "We want you to pay.\nGive in."
4. **Pay or Stay Focused**: White pill button to pay, or click "I'LL STAY FOCUSED"
5. **If You Pay**: Checkmark appears, then "Weak." in 72sp italic serif
6. **Watch The Progress**: "UNLOCKING INSTAGRAM..." with countdown and progress bar
7. **Feel The Shame**: You paid. The app knows. You know.

## 🚀 Setup Instructions

### Prerequisites

- Android Studio (Arctic Fox or newer)
- Android SDK 24+
- JDK 17+

### Building the App

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/dollar-block.git
   cd dollar-block
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned directory

3. **Sync Gradle**
   - Android Studio will automatically prompt to sync
   - Wait for dependencies to download

4. **Run the app**
   - Connect your Android device or start an emulator
   - Click the "Run" button (or press Shift+F10)

### Setting up Stripe (for real payments)

The app currently uses a mock payment system for testing. To enable real payments:

1. **Get Stripe API keys**
   - Sign up at [stripe.com](https://stripe.com)
   - Get your test mode publishable key from the [API keys page](https://dashboard.stripe.com/apikeys)

2. **Update the PaymentManager**
   - Open `app/src/main/java/com/dollarblock/app/payment/PaymentManager.kt`
   - Replace `pk_test_YOUR_KEY_HERE` with your actual Stripe publishable key

3. **Set up backend** (required for production)
   - Create a backend server to handle payment intents
   - Update the payment flow to use your backend
   - See [Stripe's Android documentation](https://stripe.com/docs/payments/accept-a-payment?platform=android)

## 📱 Installation on Device

### Required Permissions

After installing, you need to enable accessibility service:

1. Open the app
2. Tap the Settings icon (top right)
3. Find "Dollar Block" in the Accessibility Services list
4. Toggle it ON
5. Confirm the permission dialog

**Why accessibility?** This permission allows Dollar Block to detect when you open blocked apps in real-time.

## 🏗️ Architecture

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material 3
- **Architecture**: MVVM (Model-View-ViewModel)
- **Database**: Room (SQLite)
- **Payment**: Stripe Android SDK
- **App Detection**: AccessibilityService

### Project Structure

```
app/src/main/java/com/dollarblock/app/
├── data/              # Database entities and DAOs
├── payment/           # Payment processing logic
├── repository/        # Data layer
├── service/           # Background services
├── ui/theme/          # Compose theme
├── viewmodel/         # ViewModels
├── MainActivity.kt    # Main screen
└── UnblockActivity.kt # Payment prompt
```

## 🎨 Design Philosophy

**Luxury Brutalism**: Full black backgrounds, sophisticated serif typography, brutal messaging. This looks like a $100 app that calls you pathetic.

**Editorial Design**: Inspired by high-end magazines and Claude's interface. Large serif fonts (48-72sp), generous whitespace, italic emphasis.

**Psychological Warfare**: Two-stage shame system:
1. Pre-payment: Sophisticated taunt to prevent payment
2. Post-payment: Elegant humiliation with progress bar

**No Friction**: Toggle switches, not taps. White pill buttons. Everything obvious.

### The Taunt System

**Pre-Payment Screen**:
- Full black background
- "LOCKED" indicator at top
- Large serif taunt (48-52sp): "We want you to pay.\nGive in."
- Subtitle question: "Is scrolling really worth a dollar?"
- White rounded pill: "Pay $1.00 to Unblock"
- Bottom text: "I'LL STAY FOCUSED"

**Sample Pre-Payment Taunts**:
- "We want you to pay.\nGive in." / "Is scrolling really worth a dollar?"
- "Do it.\nWe dare you." / "Your discipline is worth less than $1?"
- "Your future self\nis watching." / "Make them proud or disappoint them."
- "Champions don't cave.\nYou will." / "We knew you'd be back."

**Post-Payment Screen**:
- Italic serif (72sp): "Weak."
- Caps subtitle: "PREDICTABLE OUTCOME."
- White circular checkmark
- "-$1.00 deducted."
- Progress bar: "UNLOCKING INSTAGRAM..."
- Countdown: "Redirecting in 25s"

**Sample Post-Payment Shames**:
- "Weak." / "PREDICTABLE OUTCOME."
- "Pathetic." / "EXPECTED BEHAVIOR."
- "Soft." / "ZERO DISCIPLINE."
- "Typical." / "NO SELF-CONTROL."

**The Experience**: Feel like you're being judged by a sophisticated AI therapist who's disappointed in you.

## 🧪 Testing

### Test Mode

By default, the app runs in test mode with simulated payments. You can:
- Block/unblock apps freely
- Test the UI flow
- See how the blocking works

### Testing Real Payments

1. Use Stripe test cards: `4242 4242 4242 4242`
2. Set up webhook endpoints for your backend
3. Monitor transactions in the Stripe Dashboard

## 🤝 Contributing

This is a simple, focused project. If you want to contribute:

1. Keep it simple
2. Maintain the minimalist design
3. Don't add unnecessary features
4. Test on real devices

## ⚠️ Known Limitations

- **Android 5.0+ only**: Requires AccessibilityService APIs
- **Accessibility required**: Must grant permission for app blocking
- **Not foolproof**: Tech-savvy users can disable the service
- **Battery usage**: AccessibilityService runs in the background

## 📄 License

MIT License - feel free to use this however you want!

## 💡 Inspiration

Inspired by commitment apps like:
- **Nuj Alarm Clock**: Charges you if you don't wake up
- **Alarmy**: Penalizes turning off your phone
- **Forest**: Makes you think twice before leaving the app

## 🙏 Acknowledgments

Built with:
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Stripe Android SDK](https://github.com/stripe/stripe-android)
- [Room Database](https://developer.android.com/training/data-storage/room)
- Material Design 3

---

**Made with 💚 for people who want to stay focused**

*Remember: The best way to beat a bad habit is to make it expensive!*
