# Jenkins Webhooks and Email Notifications Setup Guide

## 📧 Email Notifications Setup

### 1. Configure Jenkins Email Settings

1. Go to **Jenkins Dashboard** → **Manage Jenkins** → **Configure System**

2. Scroll to **Extended E-mail Notification** section:
   - **SMTP server**: `smtp.gmail.com` (for Gmail)
   - **SMTP Port**: `465` (SSL) or `587` (TLS)
   - **Use SSL**: Check if using port 465
   - **Use TLS**: Check if using port 587
   - **Credentials**: Click "Add" to add your email credentials
     - For Gmail: Use App Password (not regular password)
     - Username: your-email@gmail.com
     - Password: your app-specific password

3. Scroll to **E-mail Notification** section:
   - **SMTP server**: `smtp.gmail.com`
   - **Advanced**: Click to expand
   - **Use SMTP Authentication**: Check
   - **User Name**: your-email@gmail.com
   - **Password**: your app-specific password
   - **Use SSL**: Check
   - **SMTP Port**: `465`
   - **Test configuration**: Send test email to verify

### 2. Gmail App Password Setup

1. Go to your Google Account settings
2. Navigate to **Security**
3. Enable **2-Step Verification** (if not already enabled)
4. Go to **App passwords**
5. Generate a new app password for "Jenkins"
6. Copy the 16-character password
7. Use this password in Jenkins credentials

### 3. Update Jenkinsfile Email Address

In your Jenkinsfile, replace `your-email@example.com` with your actual email address:

```groovy
to: 'your-actual-email@example.com'
```

### 4. Install Required Plugins

Ensure these plugins are installed:
- **Email Extension Plugin** (for emailext functionality)
- **Mailer Plugin** (for basic email)

Go to **Manage Jenkins** → **Manage Plugins** → **Available** → Search and install

---

## 🔗 GitHub Webhooks Setup

### 1. Configure GitHub Repository Webhook

1. Go to your GitHub repository: `https://github.com/nikimanvi/Maven_jenkins_project`

2. Click **Settings** → **Webhooks** → **Add webhook**

3. Configure the webhook:
   - **Payload URL**: `http://YOUR_JENKINS_URL/github-webhook/`
     - Example: `http://jenkins.example.com/github-webhook/`
     - For local testing: `http://your-ip:8080/github-webhook/`
   - **Content type**: `application/json`
   - **Secret**: (Optional, but recommended for security)
   - **Which events**: Select "Just the push event" or customize
   - **Active**: Check this box
   - Click **Add webhook**

### 2. Configure Jenkins for GitHub Integration

1. Install **GitHub Plugin**:
   - Go to **Manage Jenkins** → **Manage Plugins** → **Available**
   - Search for "GitHub Plugin" and install

2. Configure GitHub Server:
   - Go to **Manage Jenkins** → **Configure System**
   - Scroll to **GitHub** section
   - Add GitHub Server (if not already configured)
   - Add credentials (Personal Access Token from GitHub)

3. Configure Your Jenkins Job:
   - In your pipeline configuration
   - Under **Build Triggers**, check:
     - ✅ **GitHub hook trigger for GITScm polling**

### 3. GitHub Personal Access Token (for Jenkins)

1. Go to GitHub → **Settings** → **Developer settings** → **Personal access tokens** → **Tokens (classic)**
2. Click **Generate new token (classic)**
3. Give it a name (e.g., "Jenkins Webhook")
4. Select scopes:
   - ✅ `repo` (Full control of private repositories)
   - ✅ `admin:repo_hook` (Read/write repository hooks)
5. Generate and copy the token
6. Add to Jenkins credentials

### 4. Testing the Webhook

After setting up:

1. Make a change to your repository and push:
   ```bash
   git add .
   git commit -m "Test webhook trigger"
   git push origin nikithabranch1
   ```

2. Check GitHub webhook:
   - Go to **Settings** → **Webhooks**
   - Click on your webhook
   - Check **Recent Deliveries** tab
   - Should show successful delivery (green checkmark)

3. Check Jenkins:
   - Your build should trigger automatically
   - Check build history for new build

---

## 🧪 Testing Email Notifications

### Test Scenarios:

1. **Success Email**: 
   ```bash
   # Make a small change and push
   echo "# Test" >> README.md
   git add README.md
   git commit -m "Test success notification"
   git push
   ```

2. **Failure Email**:
   ```java
   // In AppTest.java, add a failing test temporarily
   @Test
   public void testFail() {
       assertTrue(false); // This will fail
   }
   ```
   Push the change to trigger a failed build

3. **Unstable Email**:
   - Create test failures that don't break the build

---

## 🔧 Troubleshooting

### Email Not Sending:
- Check Jenkins system log: **Manage Jenkins** → **System Log**
- Verify SMTP settings are correct
- Ensure app password is correct (not regular password)
- Check firewall/network allows SMTP connections
- Test with simple `mail` instead of `emailext` first

### Webhook Not Triggering:
- Ensure Jenkins URL is publicly accessible
- Check GitHub webhook delivery status
- Verify "GitHub hook trigger" is enabled in job
- Check Jenkins logs for incoming webhook calls
- Ensure firewall allows incoming connections

### Using ngrok for Local Testing:
If Jenkins is running locally and not publicly accessible:

```bash
# Install ngrok and create tunnel
ngrok http 8080

# Use the ngrok URL in GitHub webhook
# Example: https://abc123.ngrok.io/github-webhook/
```

---

## 📝 Alternative Email Configurations

### Using Simple Mail Command:

```groovy
post {
    always {
        mail to: 'your-email@example.com',
             subject: "Build ${env.BUILD_NUMBER} - ${currentBuild.currentResult}",
             body: "Build ${env.BUILD_NUMBER} finished with status: ${currentBuild.currentResult}\n\nCheck: ${env.BUILD_URL}"
    }
}
```

### Using Other SMTP Servers:

**Outlook/Office365:**
- SMTP server: `smtp.office365.com`
- Port: `587`
- Use TLS: Yes

**Yahoo:**
- SMTP server: `smtp.mail.yahoo.com`
- Port: `587` or `465`

**Custom SMTP:**
- Use your organization's SMTP server details
- Contact your IT department for settings

---

## 🎯 Next Steps

1. ✅ Update email addresses in Jenkinsfile
2. ✅ Configure SMTP settings in Jenkins
3. ✅ Install required plugins
4. ✅ Set up GitHub webhook
5. ✅ Test by making a commit
6. ✅ Verify emails are received
7. ✅ Check webhook triggers build

Good luck with your Jenkins practice! 🚀
