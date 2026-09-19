/**
 * GET /api/health — health check
 */
export default function handler(req, res) {
  res.status(200).json({
    status:  'ok',
    app:     'Project 4R API',
    version: '1.0.0',
    endpoints: [
      'GET /api/flights?origin=DXB&destination=KTM&date=YYYY-MM-DD',
      'GET /api/health'
    ]
  });
}
