import Link from 'next/link';

export default function Home() {
  return (
    <div className="container section" style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', textAlign: 'center', gap: '24px', paddingTop: '120px' }}>
      <div className="badge badge-primary anim-fade-up" style={{ animationDelay: '0.1s' }}>v1.0 is Live</div>
      
      <h1 className="heading-display anim-fade-up" style={{ maxWidth: '800px', animationDelay: '0.2s' }}>
        Your Personal AI Fitness Coach
      </h1>
      
      <p className="heading-3 text-muted anim-fade-up" style={{ maxWidth: '600px', fontWeight: 400, animationDelay: '0.3s' }}>
        Get customized workout and diet plans. Chat with your AI coach anytime. Track your progress every week. All driven by real data.
      </p>

      <div className="anim-fade-up" style={{ display: 'flex', gap: '16px', marginTop: '24px', animationDelay: '0.4s' }}>
        <Link href="/auth/register" className="btn btn-primary btn-lg">Start Your Journey</Link>
        <Link href="/auth/login" className="btn btn-ghost btn-lg">Log In</Link>
      </div>

      <div className="anim-fade-up" style={{ marginTop: '80px', animationDelay: '0.6s', width: '100%', maxWidth: '900px' }}>
        <div className="form-row-3">
          <div className="card">
            <h3 className="heading-3 text-gradient" style={{ marginBottom: '8px' }}>Personalized Plans</h3>
            <p className="text-muted text-sm">Diet and workout plans tailored to your exact stats and goals.</p>
          </div>
          <div className="card">
            <h3 className="heading-3 text-gradient" style={{ marginBottom: '8px' }}>Chat Coach</h3>
            <p className="text-muted text-sm">Ask questions, get advice, and stay motivated with your AI trainer.</p>
          </div>
          <div className="card">
            <h3 className="heading-3 text-gradient" style={{ marginBottom: '8px' }}>Progress Tracking</h3>
            <p className="text-muted text-sm">Log your weight and energy. The AI analyzes your weekly trends.</p>
          </div>
        </div>
      </div>
    </div>
  );
}
