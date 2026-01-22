# 🧪 Testing Guide - Dollar Block Mockup Build

This is the **simplified mockup version** - no payment processing, just pure UI/UX flow.

## 🎯 What You'll See

### 1. Onboarding Screen (First Launch)
```
┌────────────────────────────┐
│                            │
│   Discipline is            │
│   cheap.                   │
│                            │
│   Distraction              │ ← Italic serif
│   costs $1.                │
│                            │
│   1.  Choose apps.         │
│   2.  Pay $1 to open.      │
│   3.  Build focus.         │
│                            │
│  ┌──────────────────────┐  │
│  │ Link Card & Start  → │  │ ← White button
│  └──────────────────────┘  │
│   COMMIT TO THE COST       │
└────────────────────────────┘
```

### 2. App Selection Screen
```
┌────────────────────────────┐
│  Choose your poison.       │ ← 48sp Italic Serif
│  SELECT APPS TO BLOCK      │
│                            │
│  Instagram          ⚪─○   │
│  TikTok             ⚫─○   │ ← Toggle ON
│  Twitter            ⚪─○   │
│  YouTube            ⚫─○   │
│  Reddit             ⚪─○   │
│  Facebook           ⚪─○   │
│                            │
│  ┌──────────────────────┐  │
│  │       DONE           │  │
│  └──────────────────────┘  │
└────────────────────────────┘
```

### 3. Block Screen (When Opening Blocked App)
```
┌────────────────────────────┐
│ 🔒 LOCKED               •••│
│                            │
│                            │
│   We want you to pay.      │ ← 48sp Serif
│   Give in.                 │ ← 52sp Italic
│                            │
│ Is scrolling really worth  │
│ a dollar?                  │
│                            │
│  ┌──────────────────────┐  │
│  │ Pay $1.00 to Unblock │  │
│  └──────────────────────┘  │
│                            │
│   I'LL STAY FOCUSED        │ ← Click to close
└────────────────────────────┘
```

### 4. Shame Screen (After "Paying")
```
┌────────────────────────────┐
│                            │
│        Weak.               │ ← 72sp Italic
│  PREDICTABLE OUTCOME.      │
│                            │
│         ╔════╗             │
│         ║ ✓  ║             │ ← Bounces in
│         ╚════╝             │
│                            │
│   -$1.00 deducted.         │
│                            │
│ UNLOCKING      Redirecting │
│ INSTAGRAM...   in 23s      │
│ ▓▓▓▓▓░░░░░░░░░░░░░░░░░░   │ ← Fills up
└────────────────────────────┘
```

## ⚡ Quick Test Steps

### Step 1: Install & Run
```bash
# In Android Studio:
1. Open project folder: dollar-block/
2. Wait for Gradle sync
3. Click Run (green ▶️ button)
4. Select emulator or device
```

### Step 2: Complete Onboarding
1. App launches → Onboarding screen
2. Read the philosophy
3. Click "Link Card & Start Blocking →"

### Step 3: Select Apps
1. Toggle ON apps you want to block (e.g., TikTok, Instagram)
2. Click "DONE"

### Step 4: Enable Accessibility Service
**CRITICAL - App won't work without this:**

1. Go to device Settings
2. Navigate to: **Accessibility**
3. Find **Dollar Block**
4. Toggle it **ON**
5. Confirm the permission dialog

### Step 5: Test Blocking
1. Press Home button
2. Try to open Instagram (or whatever app you blocked)
3. **Result**: Full-screen block overlay appears!

### Step 6: Test Flow
**Option A - Stay Strong:**
- Click "I'LL STAY FOCUSED" → Returns to home

**Option B - Give In:**
- Click "Pay $1.00 to Unblock"
- Button shows spinner (1 second)
- Shame screen appears: "Weak."
- Checkmark bounces in
- Progress bar counts down 25 → 0
- App unblocks and closes overlay

## 🎨 What's Different From Production?

| Feature | This Build | Production |
|---------|-----------|------------|
| Payment | ❌ Simulated | ✅ Real Stripe |
| Button | Waits 1 sec | Processes card |
| Unblock | Always works | Only if payment succeeds |
| Testing | Easy & fast | Requires backend |

## 🐛 Troubleshooting

### "App doesn't block anything"
→ **Fix**: Enable Accessibility Service (Step 4 above)

### "Can't find Accessibility option"
→ **Fix**: Settings → Search for "Accessibility" → Dollar Block

### "App crashes when opening blocked app"
→ **Fix**: Ensure minSdk is 24+ in `build.gradle.kts`

### "Shame screen doesn't show"
→ **Fix**: Make sure you clicked the white "Pay $1.00" button

### "Progress bar doesn't animate"
→ **Check**: Android version is API 24+ (Android 7.0+)

## 📱 Recommended Test Devices

**Emulator:**
- Pixel 5 - API 34 (Android 14) ✅ Best
- Pixel 6 - API 33 (Android 13) ✅ Good
- Any device - API 24+ (Android 7.0+) ✅ Works

**Real Device:**
- Any Android 7.0+ phone
- USB Debugging enabled
- Developer Mode activated

## 🎯 What to Look For

### ✅ Things That Should Work:
- Onboarding flow is smooth
- App selection toggles work
- Accessibility service enables
- Blocked apps trigger overlay
- "I'LL STAY FOCUSED" closes overlay
- "Pay $1.00" shows loading → shame screen
- Progress bar animates smoothly
- Countdown timer updates
- App unblocks after shame screen
- All text is readable (serif fonts)
- Black backgrounds throughout
- White buttons are obvious

### ❌ Things That Won't Work:
- Real payment processing (not implemented)
- Credit card input (not needed)
- Stripe integration (removed)
- Backend API calls (none exist)

## 📝 Notes

- This build is **intentionally simplified** for testing
- No payment logic = faster iteration & testing
- All UI/UX matches the mockups exactly
- Accessibility service is required for blocking to work
- The "payment" button just simulates a 1-second delay

## 🚀 Next Steps

After testing this mockup:
1. If UI looks good → Add real Stripe payment
2. If flow works well → Deploy backend API
3. If blocking works → Add analytics
4. If all good → Build release APK

---

**Current Branch**: `claude/dollar-block-mockups-Wp6zC`

**To test**: Open in Android Studio and click Run!
