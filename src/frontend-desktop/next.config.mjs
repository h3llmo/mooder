/** @type {import('next').NextConfig} */
const nextConfig = {
  // Rewrites: in docker, API calls from the browser go to nginx (/api/...)
  // which proxies to the backend. In standalone dev (no docker), point to
  // the backend directly via MOODER_API_URL.
  async rewrites() {
    return [
      {
        source: "/api/:path*",
        destination: `${process.env.MOODER_API_URL ?? "http://localhost:8080"}/api/:path*`,
      },
    ];
  },
  // Output standalone for Docker — minimises image size
  output: "standalone",
};

export default nextConfig;
