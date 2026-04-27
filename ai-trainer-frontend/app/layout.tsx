import type { Metadata } from "next";
import "./globals.css";
import Navbar from "@/components/Navbar";
import { Toaster } from 'react-hot-toast';

export const metadata: Metadata = {
  title: "AI Personal Trainer",
  description: "Your personalized AI fitness coach",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body>
        <div className="page">
          <Navbar />
          <main style={{ flex: 1 }}>
            {children}
          </main>
        </div>
        <Toaster 
          position="bottom-center"
          toastOptions={{
            style: {
              background: 'var(--bg-3)',
              color: 'var(--text-1)',
              border: '1px solid var(--border)',
            },
            success: {
              iconTheme: {
                primary: 'var(--green)',
                secondary: 'white',
              },
            },
            error: {
              iconTheme: {
                primary: 'var(--red)',
                secondary: 'white',
              },
            },
          }}
        />
      </body>
    </html>
  );
}
