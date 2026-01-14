# 💵 Dollar Block

**The simplest, most effective screen time blocker for Android**

Block distracting apps and pay $1 each time you want to unblock them. Every dollar you pay goes to charity, turning your distractions into donations.

## ✨ Features

- **Super Simple**: Just tap an app to block it. That's it.
- **$1 to Unblock**: When you try to open a blocked app, pay $1 to unlock it
- **Psychological Taunts**: 19 savage messages that shame you into NOT paying ("Go ahead, waste your dollar 💸", "Your competition is working right now 🚀", "Really? Again? 🤨")
- **Minimalist Design**: Clean, modern UI built with Jetpack Compose
- **Real-time Blocking**: Uses Android AccessibilityService for instant app detection
- **Local Storage**: Your blocked apps list is stored securely on your device
- **Stripe Integration**: Secure payment processing (test mode ready)

## 🎯 How It Works

1. **Block Apps**: Open Dollar Block and tap any app you want to block
2. **Get Interrupted**: Try to open a blocked app? Dollar Block catches you!
3. **Get Taunted**: See a random savage message designed to make you feel guilty
4. **Pay $1 or Go Back**: The app psychologically nudges you to choose discipline
5. **If You Pay**: Watch as a checkmark appears, then get DESTROYED with a post-payment shame message in elegant serif font
6. **Stay Focused**: Most users go back after the pre-payment taunt. Those who don't? They regret it.

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

**Minimalism First**: Dollar Block does ONE thing and does it well. No complex features, no cluttered UI, no unnecessary options.

**Psychological Discipline**: Unlike other blockers, this app uses taunts and guilt to make you NOT want to pay. It's a discipline tool, not a money-extraction scheme.

**Obvious Action**: The "$1 to Unblock" button is front and center. No dark patterns, no hidden costs. But the "Stay Strong - Go Back" button is equally prominent.

**Instant Feedback**: Real-time app blocking means you immediately know when you're about to break your focus.

### The Taunt System

**Pre-Payment Taunts** (19 messages):
Every time you try to unblock an app, you'll see one of these:
- "Breaking already? Weak. 💀"
- "Your goals can wait, right? ⏰"
- "Do it. I dare you. 😈"
- "This is why you're not progressing 📊"
- "Imagine explaining this to your therapist 🛋️"

**Post-Payment Shame** (13 messages):
If you actually pay, the screen goes black and shows:
- ✓ Green checkmark with satisfying bounce
- Then one word in giant elegant serif font:
  - "Weak."
  - "Pathetic."
  - "Disappointing."
  - "So predictable."
  - "You'll be back."

**The psychology**:
- Pre-payment: Loss aversion + guilt + challenge
- Post-payment: Brief satisfaction (checkmark) → immediate regret (shame)
- Result: You'll think twice before paying again

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
