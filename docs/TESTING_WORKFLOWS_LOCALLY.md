# Testing GitHub Actions Workflows Locally

## Using `act` for Local Workflow Testing

Before pushing changes that modify GitHub Actions workflows, it's best practice to test them locally using [`act`](https://github.com/nektos/act).

### Installation

```bash
# macOS
brew install act

# Linux
curl https://raw.githubusercontent.com/nektos/act/master/install.sh | sudo bash

# Windows (with Chocolatey)
choco install act-cli
```

### Basic Usage

```bash
# List all workflows
act -l

# Run all workflows
act

# Run specific workflow
act -j lint

# Run specific event
act push
act pull_request
```

### Testing Our Workflows

#### 1. Test Lint Workflow
```bash
# Test lint checks
act -j kotlin-lint
act -j typescript-lint
act -j format-check
```

#### 2. Test Build Workflow
```bash
# Test Android build
act -j build-debug-apk

# Test Functions build
act -j build-functions
```

#### 3. Test with Secrets
```bash
# Create .secrets file
echo "FIREBASE_TOKEN=your-token" > .secrets

# Run with secrets
act -s FIREBASE_TOKEN=your-token
```

### Configuration

Create `.actrc` in project root:

```
-P ubuntu-latest=catthehacker/ubuntu:act-latest
--secret-file .secrets
--env-file .env.local
```

### Limitations

- Some GitHub-specific features may not work
- Large runners may require more resources
- Firebase Emulator needs to run separately

### Recommended Workflow

1. **Before Pushing**:
   ```bash
   # Test lint locally
   act -j kotlin-lint
   act -j typescript-lint

   # Test build locally
   act -j build-debug-apk
   ```

2. **Fix Issues Locally**: Address any errors before pushing

3. **Push with Confidence**: CI will pass on first try

### Debugging Failed Workflows

```bash
# Run in verbose mode
act -v

# Run in debug mode
act --debug

# Interactive mode (step through)
act --container-architecture linux/amd64 --reuse
```

### Resources

- [act Documentation](https://github.com/nektos/act)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Nektos Act Runners](https://github.com/catthehacker/docker_images)

---

## Alternative: GitHub CLI

If `act` doesn't work, use GitHub CLI to quickly check:

```bash
# Check workflow status
gh run list

# Watch workflow in real-time
gh run watch

# View workflow logs
gh run view --log
```

---

**Best Practice**: Always test workflows locally before pushing to avoid failed CI runs and wasted GitHub Actions minutes.
