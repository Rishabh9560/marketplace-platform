# Vendor Dashboard - React Frontend

Production-grade React 19 frontend for the Vendor Hub marketplace management platform.

## 🚀 Features

- **Authentication**: OAuth2/JWT-based login with persistent sessions
- **Vendor Dashboard**: Real-time metrics, earnings, and performance analytics
- **Product Management**: Create, edit, and manage product listings with inventory tracking
- **KYC Verification**: Document upload and verification workflow
- **Payout History**: Transaction tracking and payment details
- **Responsive Design**: Mobile-first UI with Tailwind CSS
- **Type-Safe**: Full TypeScript implementation with strict mode
- **State Management**: Lightweight Zustand for efficient state handling
- **API Integration**: Axios with interceptors for seamless backend communication

## 🛠️ Tech Stack

- **Framework**: React 19
- **Build Tool**: Vite 5
- **Language**: TypeScript
- **Styling**: Tailwind CSS
- **State Management**: Zustand
- **HTTP Client**: Axios
- **Routing**: React Router 6
- **Charts**: Recharts
- **Icons**: Lucide React
- **Date Formatting**: date-fns

## 📦 Installation

### Prerequisites

- Node.js 18+
- npm or yarn

### Setup

```bash
# Install dependencies
npm install

# Create environment file
cp .env.example .env

# Update API URL in .env if needed
VITE_API_URL=http://localhost:8080/api/v1
```

## 🚀 Development

```bash
# Start development server (runs on http://localhost:3000)
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview

# Run type checking
npm run type-check

# Format code
npm run format

# Lint code
npm run lint
```

## 📁 Project Structure

```
src/
├── components/          # Reusable UI components
│   ├── Navigation.tsx   # Top navigation bar
│   ├── Footer.tsx       # Footer component
│   └── common/          # Shared components (Card, Button, Input, etc.)
├── layouts/            # Layout components
│   ├── MainLayout.tsx   # Main app layout with nav + footer
│   └── AuthLayout.tsx   # Auth page layout
├── pages/              # Page components
│   ├── LoginPage.tsx
│   ├── RegisterPage.tsx
│   ├── DashboardPage.tsx
│   ├── ProductListingsPage.tsx
│   ├── CreateProductPage.tsx
│   ├── EditProductPage.tsx
│   ├── PayoutHistoryPage.tsx
│   ├── KYCVerificationPage.tsx
│   └── ProfilePage.tsx
├── store/              # Zustand state management
│   └── index.ts        # Auth and Dashboard stores
├── hooks/              # Custom React hooks
│   └── useApi.ts       # API calls and data fetching
├── lib/
│   ├── apiClient.ts    # Axios HTTP client
│   └── utils.ts        # Utility functions
├── types/              # TypeScript type definitions
│   └── index.ts        # All interfaces
├── App.tsx             # Main app component with routing
├── main.tsx            # Entry point
└── index.css           # Global styles
```

## 🔐 Authentication

The application uses JWT-based authentication:

1. Users login with email/password
2. Backend returns JWT token
3. Token is stored in localStorage
4. Axios interceptor automatically adds token to all requests
5. 401 responses trigger logout and redirect to login page

## 🔗 API Integration

All API calls go through the centralized `apiClient`:

```typescript
// Example: Fetching vendor profile
const response = await apiClient.get<VendorProfile>(
  `/vendors/${vendorId}`
)
```

Custom hooks provide specialized interfaces for each feature:

```typescript
// Example: Using custom hook
const { data: listings, loading, execute: fetchListings } = useProductListings()
await fetchListings(vendorId, 1, 10)
```

## 🎨 Styling

- **Tailwind CSS**: Utility-first CSS framework
- **Custom Theme**: Primary (Blue), Secondary (Green), Danger (Red), Warning (Orange)
- **Responsive**: Mobile-first design that adapts to all screen sizes
- **Dark Mode**: Configured but optional

## 📊 Components

### UI Components (`src/components/common`)

- **Card**: Container component with optional hover effect
- **Button**: Multiple variants (primary, secondary, danger, ghost) and sizes (sm, md, lg)
- **Input**: Text input with label, error display, and icon support
- **Select**: Dropdown component with options
- **Badge**: Status badges with 5 variants
- **Dialog**: Modal dialog for confirmations
- **Tabs**: Tabbed interface

### Layout Components

- **MainLayout**: Wraps authenticated pages with Navigation + Footer
- **AuthLayout**: Wraps auth pages with branded layout

## 🔄 State Management

### AuthStore

```typescript
const { isAuthenticated, vendor, token, login, logout, setVendor } = useAuthStore()
```

### DashboardStore

```typescript
const { summary, loading, error, setSummary, setLoading, setError } = useDashboardStore()
```

## 🧪 Type Safety

All data structures are defined in `src/types/index.ts`:

- `VendorProfile`: Vendor account information
- `ProductListing`: Product inventory and pricing
- `VendorPayoutRecord`: Payment transactions
- `VendorStatistics`: Performance metrics
- And more...

## 📝 Environment Variables

Create `.env` file in project root:

```env
VITE_API_URL=http://localhost:8080/api/v1
```

## 🚀 Deployment

### Build for Production

```bash
npm run build
```

This creates an optimized `dist/` folder ready for deployment.

### Production Considerations

- Update `VITE_API_URL` environment variable for production API
- Ensure CORS is properly configured on backend
- Consider implementing error boundary components
- Set up analytics and monitoring
- Configure CDN for static assets
- Implement service workers for PWA capabilities

## 🐛 Troubleshooting

### CORS Errors

Ensure backend allows requests from frontend origin. Configure in Spring Security:

```java
@Configuration
public class CorsConfig {
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        config.setAllowedMethods(Arrays.asList("*"));
        config.setAllowCredentials(true);
        config.setAllowedHeaders(Arrays.asList("*"));
        // ...
    }
}
```

### Token Expiration

Implement token refresh logic in axios interceptor to handle expired tokens gracefully.

### Environment Issues

Ensure `.env` file exists and `VITE_API_URL` points to correct backend instance.

## 📚 Documentation

- [React Documentation](https://react.dev)
- [Vite Documentation](https://vitejs.dev)
- [Tailwind CSS Documentation](https://tailwindcss.com)
- [TypeScript Documentation](https://www.typescriptlang.org)
- [Zustand Documentation](https://github.com/pmndrs/zustand)
- [Axios Documentation](https://axios-http.com)

## 📄 License

Copyright © 2024 Vendor Hub. All rights reserved.

## 👥 Support

For issues and feature requests, please contact the development team.
