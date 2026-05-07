-- Seed data for auction app
-- Note: UUIDs are hardcoded for reproducibility in dev/test environments

INSERT INTO users (id, username, email, created_at) VALUES
('550e8400-e29b-41d4-a716-446655440001', 'seller1', 'seller1@example.com', NOW()),
('550e8400-e29b-41d4-a716-446655440002', 'bidder1', 'bidder1@example.com', NOW()),
('550e8400-e29b-41d4-a716-446655440003', 'bidder2', 'bidder2@example.com', NOW()),
('550e8400-e29b-41d4-a716-446655440004', 'seller2', 'seller2@example.com', NOW());

-- Insert test auctions
INSERT INTO auctions (id, seller_id, item_name, item_category, auction_type, starting_bid, bin_price, current_bid, highest_bidder_id, start_time, end_time, status, claimed, version) VALUES
('650e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', 'Enchanted Diamond Sword', 'weapons', 'AUCTION', 1000, NULL, 1000, NULL, NOW(), NOW() + INTERVAL '24 hours', 'ACTIVE', false, 0),
('650e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440001', 'Golden Apple', 'consumables', 'BIN', NULL, 500, 500, NULL, NOW(), NOW() + INTERVAL '12 hours', 'ACTIVE', false, 0),
('650e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440004', 'Emerald Block Stack', 'blocks', 'AUCTION', 5000, NULL, 5000, NULL, NOW(), NOW() + INTERVAL '48 hours', 'ACTIVE', false, 0);

-- Insert test bids
INSERT INTO bids (id, auction_id, bidder_id, amount, timestamp) VALUES
('750e8400-e29b-41d4-a716-446655440001', '650e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440002', 1200, NOW() - INTERVAL '1 hour'),
('750e8400-e29b-41d4-a716-446655440002', '650e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440003', 1500, NOW() - INTERVAL '30 minutes');
